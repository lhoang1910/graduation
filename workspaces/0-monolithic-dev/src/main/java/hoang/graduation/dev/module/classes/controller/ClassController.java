package hoang.graduation.dev.module.classes.controller;
import hoang.graduation.dev.module.classes.service.ClassService;
import hoang.graduation.dev.module.classes.service.ClassView;
import hoang.graduation.dev.page.SearchListClassRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import hoang.graduation.share.model.request.classes.CreateClassRequest;
import hoang.graduation.share.model.response.WrapResponse;


@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;
    private final ClassView classView;

    @PostMapping("/create")
    public WrapResponse<?> createClass(@RequestBody CreateClassRequest request) {
        return classService.create(request);
    }

    @PutMapping("/{id}/update")
    public WrapResponse<?> updateClass(@PathVariable String id, @RequestBody CreateClassRequest request) {
        return classService.update(id, request);
    }

    @DeleteMapping("/{id}/delete")
    public WrapResponse<?> deleteClass(@PathVariable String id) {
        return classService.delete(id);
    }

    @PostMapping("/{classId}/join")
    public WrapResponse<?> joinClass(@PathVariable String classId) {
        return classService.joinClass(classId);
    }

    @PostMapping("/{classId}/add")
    public WrapResponse<?> addToClass(@RequestBody String email, @PathVariable String classId) {
        return classService.addToClass(email, classId);
    }

    @PostMapping("/{classId}/remove")
    public WrapResponse<?> deleteFromClass(@RequestBody String email, @PathVariable String classId) {
        return classService.deleteFromClass(email, classId);
    }

    @PostMapping("/{classId}/out")
    public WrapResponse<?> outClass(@PathVariable String classId) {
        return classService.outClass(classId);
    }

    @PostMapping("/")
    public WrapResponse<?> getAllClass(@RequestBody SearchListClassRequest request) {
        return classView.searchClassList(request);
    }

    @GetMapping("/{id}")
    public WrapResponse<?> getAllClass(@PathVariable String id) {
        return classView.getDetail(id);
    }
}
