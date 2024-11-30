package hoang.graduation.dev.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ExcelUtils {
    public static String getValue(XSSFCell inputCell) {
        String input = inputCell == null || inputCell.getCellType() == CellType.BLANK ? null : getValueCell(inputCell);
        return input == null ? "" : input.trim();
    }

    private static String getValueCell(XSSFCell inputCell) {
        if (inputCell.getCellType() != CellType.STRING
                && inputCell.getCellType() != CellType.FORMULA
                && DateUtil.isCellDateFormatted(inputCell)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            return sdf.format(inputCell.getDateCellValue());
        } else if (inputCell.getCellType() == CellType.NUMERIC) {
            DecimalFormat df = new DecimalFormat();
            df.setGroupingUsed(false);
            return df.format(inputCell.getNumericCellValue());
        }
        return inputCell.getStringCellValue();
    }

    public static boolean isRowEmpty(Row row, int length) {
        for (int c = row.getFirstCellNum(); c < length; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && cell.getCellType() != CellType._NONE)
                return false;
        }
        return true;
    }
}
