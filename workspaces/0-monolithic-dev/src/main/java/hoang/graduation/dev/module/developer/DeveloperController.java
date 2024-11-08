package hoang.graduation.dev.module.developer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class DeveloperController {
    private static final String LOG_FILE_PATH = "../app.log";

    @GetMapping("/api/logs")
    public String getLogs(@RequestParam(defaultValue = "100") int lines) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(LOG_FILE_PATH));
        int start = Math.max(allLines.size() - lines, 0);
        List<String> lastLines = allLines.subList(start, allLines.size());

        return String.join("\n", lastLines);
    }
}
