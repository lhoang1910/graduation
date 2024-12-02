package hoang.graduation.dev.module.user.service;

import hoang.graduation.dev.component.EmailUtils;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.classes.entity.ClassEntity;
import hoang.graduation.dev.module.classes.repo.ClassRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.dev.share.constant.Role;
import hoang.graduation.dev.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.dev.share.exceptions.DataNotFoundException;
import hoang.graduation.dev.share.model.request.user.ChangePasswordRequest;
import hoang.graduation.dev.share.model.request.user.CreateUserRequest;
import hoang.graduation.dev.share.model.request.user.ImportUserRequest;
import hoang.graduation.dev.share.model.request.user.UpdateUserRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
import hoang.graduation.dev.share.model.response.user.ImportUserResponse;
import hoang.graduation.dev.share.utils.DateTimesUtils;
import hoang.graduation.dev.share.utils.MappingUtils;
import hoang.graduation.dev.share.utils.ValidationUtils;
import hoang.graduation.dev.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.service.spi.ServiceException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    protected final transient Logger logger = LogManager.getLogger(this.getClass());
    private final LocalizationUtils localizationUtils;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate template;
    private final UserRepo userRepo;
    private final EmailUtils emailUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final ClassRepo classRepo;

    public WrapResponse<?> createUser(CreateUserRequest request){
        //register user
        if (!request.getEmail().isBlank() && userRepo.existsByEmail(request.getEmail())) {
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.USER_EMAIL_EXISTS)).build();
        }

        if (!ValidationUtils.isValidEmail(request.getEmail())) {
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_EMAIL)).build();
        }

        if (!request.getPassword().equals(request.getRetypePassword())) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build();
        }


        UserEntity newUser = MappingUtils.mapObject(request, UserEntity.class);
        newUser.setId(UUID.randomUUID().toString());
        newUser.setActive(true);
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());
        if (!request.isSocialLogin()) {
            String password = request.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        UserEntity savedUser = userRepo.save(newUser);
        template.convertAndSend(RabbitQueueMessage.QUEUE_SEND_CREATE_USER, savedUser);
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(savedUser)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ACCOUNT_REGISTRATION_SUCCESS))
                .build();
    }

    public UserEntity updateUser(String userId, UpdateUserRequest request) throws Exception {
        // Find the existing user by userId
        UserEntity existingUser = userRepo.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));

        if (StringUtils.isNotBlank(request.getFullName())) {
            existingUser.setFullName(request.getFullName());
        }
        if (request.getBirthDay() != null) {
            existingUser.setBirthDay(request.getBirthDay());
        }
        if (StringUtils.isNotBlank(request.getPhoneNumber()) && !userRepo.existsByPhoneNumber(request.getPhoneNumber())){
            existingUser.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.isGoogleAccountIdValid()) {
            existingUser.setGoogleAccountId(request.getGoogleAccountId());
        }
        UserEntity savedUser = userRepo.save(existingUser);
        template.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_USER, savedUser);
        return savedUser;
    }

    public WrapResponse<?> changePassword(String userId, ChangePasswordRequest request){

        UserEntity user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_PASSWORD))
                    .build();
        }
        if (!request.getPassword().equals(request.getRetypePassword())) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build();
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);
        return WrapResponse.builder().isSuccess(true).status(HttpStatus.OK).message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_RESET_SUCCESSFULLY)).build();
    }

    public void changeAvatar(String userId, String imageName) throws Exception {
        UserEntity existingUser = userRepo.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));
        existingUser.setAvatar(imageName);
        userRepo.save(existingUser);
    }

    public WrapResponse<?> importClassUser(String id, byte[] bytes) throws IOException {
        if (!classRepo.existsById(id)){
            throw new ServiceException("Class + " + id + " không tồn tại");
        }
        logger.info(">>>>>>>>>>>>>>>>>> importing user into class");
        int sheetNumber = 0;
        int totalColumn = 4;
        ByteArrayInputStream ips = new ByteArrayInputStream(bytes);
        List<ImportUserRequest> rows = new LinkedList<>();
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(ips);
        } catch (Exception e) {
            logger.error("Error on import {}", e.getMessage());
            throw new ServiceException("Error on import");
        }
        if (workbook.getNumberOfSheets() == 0) {
            throw new ServiceException("Error on import");
        }
        XSSFSheet worksheet1 = workbook.getSheetAt(sheetNumber);
        int rowCount = worksheet1.getLastRowNum() + 1;
        rowCount = Math.min(rowCount, worksheet1.getPhysicalNumberOfRows());
        int colCount = 0;
        if (rowCount > 0) {
            colCount = worksheet1.getRow(0).getPhysicalNumberOfCells();
        }
        if (rowCount == 0 || colCount < totalColumn) {
            throw new ServiceException("Error on import");
        }

        List<ImportUserResponse> returnValue = new ArrayList<>();
        boolean isSuccess = true;
        try {
            for (int i = 1; i < rowCount; i++) {
                XSSFRow row = worksheet1.getRow(i);
                if (row == null || ExcelUtils.isRowEmpty(row, totalColumn)) {
                    continue;
                }
                String email = ExcelUtils.getValue(row.getCell(0));
                String password = ExcelUtils.getValue(row.getCell(1));
                String fullName = ExcelUtils.getValue(row.getCell(2));
                String dateOfBirthStr = ExcelUtils.getValue(row.getCell(3));
                Date dateOfBirth = DateTimesUtils.convertStringToDate(dateOfBirthStr, "MM/dd/yyyy");

                ImportUserRequest request = ImportUserRequest.builder()
                        .index(i)
                        .email(email)
                        .password(password)
                        .fullName(fullName)
                        .dateOfBirth(dateOfBirth)
                        .build();
                rows.add(request);
                ImportUserResponse response = updateUserInfo(request);
                if (!response.isSuccess()){
                    isSuccess = false;
                }
                logger.debug("row number {}: {}", i, request);
                returnValue.add(response);
            }
        } catch (Exception e) {
            logger.error("Error on import {}", e.getMessage());
            throw new ServiceException("Error on import");
        }

        List<UserEntity> newUsers = new ArrayList<>();
        for (ImportUserRequest request : rows) {
            if (request == null){
                continue;
            }
            newUsers.add(UserEntity.builder()
                            .id(UUID.randomUUID().toString())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .fullName(request.getFullName())
                            .birthDay(request.getDateOfBirth())
                            .role(Role.STUDENT)
                            .isActive(true)
                            .createdAt(new Date())
                            .updatedAt(new Date())
                    .build());
        }

        logger.info(">>>>>>>>>>>>>>>>> total Rows read: " + returnValue.size());
        logger.info(">>>>>>>>>>>>>>>>> total Rows error: " + returnValue.stream().filter(x -> !x.isSuccess()).count());

        if (isSuccess && CollectionUtils.isNotEmpty(newUsers)){
            List<UserEntity> users = userRepo.saveAll(newUsers);
            if (CollectionUtils.isNotEmpty(users)){
                Optional<ClassEntity> classOptional = classRepo.findById(id);
                if (classOptional.isPresent()){
                    ClassEntity classEntity = classOptional.get();
                    List<String> classUserEmails = classEntity.getUserEmails();
                    if (classUserEmails == null){
                        classUserEmails = new ArrayList<>();
                    }
                    int participantAmount = classUserEmails.size();
                    if (users.size() + participantAmount > classEntity.getLimitSlot()){
                        return WrapResponse.builder()
                                .isSuccess(false)
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Số lượng học sinh được thêm mới vượt quá giới hạn số lượng của lớp học")
                                .build();
                    }
                    for (UserEntity user : users) {
                        if (StringUtils.isBlank(user.getEmail())){
                            continue;
                        }
                        classUserEmails.add(user.getEmail());
                    }
                    classRepo.save(classEntity);
                    return WrapResponse.builder()
                            .isSuccess(true)
                            .status(HttpStatus.OK)
                            .message("import thành công")
                            .build();
                }
            }
        }

        return WrapResponse.builder()
                .isSuccess(false)
                .status(HttpStatus.BAD_REQUEST)
                .message("Có  " + returnValue.stream().filter(x -> !x.isSuccess()).count() + "/" + returnValue.size() + " dòng lỗi, vui lòng tải file xuống để xem chi tiết. Lưu ý: file chỉ tồn tại trong vòng 5 phút")
                .data(exportToExcel(returnValue))
                .build();
    }

    private ImportUserResponse updateUserInfo(ImportUserRequest request) {
        try {
            updateImportUserResponse(request);
            return ImportUserResponse.builder()
                    .index(request.getIndex())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .fullName(request.getFullName())
                    .dateOfBirth(request.getDateOfBirth())
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ImportUserResponse.builder()
                    .index(request.getIndex())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .fullName(request.getFullName())
                    .dateOfBirth(request.getDateOfBirth())
                    .success(false)
                    .errorDesc(e.getMessage())
                    .build();
        }
    }

    public void updateImportUserResponse(ImportUserRequest request) {
        if (StringUtils.isBlank(request.getEmail())){
            throw new ServiceException("Email đang bị trống");
        }
        if (userRepo.existsByEmail(request.getEmail())){
            throw new ServiceException("Email này đã tồn tại");
        }
        if (StringUtils.isBlank(request.getPassword()) || (request.getPassword().length() >= 6 && request.getPassword().length() <= 16)){
            throw new ServiceException("Mật khẩu phải có độ dài từ 6-16 kí tự");
        }
        if (StringUtils.isBlank(request.getFullName())){
            throw new ServiceException("Họ và tên đang bị trống");
        }
        if (request.getDateOfBirth() == null){
            throw new ServiceException("Ngày sinh đang bị trống");
        }
    }


    public String exportToExcel(List<ImportUserResponse> returnValue) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Import Users");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Email");
        headerRow.createCell(1).setCellValue("Mật khẩu");
        headerRow.createCell(2).setCellValue("Họ và tên");
        headerRow.createCell(3).setCellValue("Ngày sinh");
        headerRow.createCell(4).setCellValue("Lỗi");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        int rowNum = 1;
        for (ImportUserResponse userResponse : returnValue) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(userResponse.getEmail());
            row.createCell(1).setCellValue(userResponse.getPassword());
            row.createCell(2).setCellValue(userResponse.getFullName());
            row.createCell(3).setCellValue(userResponse.getDateOfBirth() != null ? dateFormat.format(userResponse.getDateOfBirth()) : "");
            row.createCell(4).setCellValue(userResponse.isSuccess() ? "Thành công" : userResponse.getErrorDesc());
        }

        String fileName = "ImportUsers_" + UUID.randomUUID().toString() + ".xlsx";

        String projectBasePath = System.getProperty("user.dir");
        String excelDirPath = Paths.get(projectBasePath, "src", "main", "resources", "excel").toString();
        File dir = new File(excelDirPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = Paths.get(excelDirPath, fileName).toString();
        File file = new File(filePath);

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (file.exists()) {
                    file.delete();
                    System.out.println("File đã được xóa sau 5 phút.");
                }
            }
        }, 300000);

        return fileName;
    }
}
