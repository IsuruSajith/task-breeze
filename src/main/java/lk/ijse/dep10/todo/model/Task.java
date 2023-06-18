package lk.ijse.dep10.todo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class Task implements Serializable {
    private int id;
    private String description;
    private Status status;
    public enum Status {
        COMPLETED, NOT_COMPLETED
    }
}
