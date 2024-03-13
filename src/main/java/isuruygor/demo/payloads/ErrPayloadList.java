package isuruygor.demo.payloads;


import java.util.List;

public record ErrPayloadList(String message,
                             List<String> errorsList) {
}
