package dev.masa.preventitempickup.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name = "prevented_items")
public class PreventedItem {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    @NonNull
    private String material;

    @DatabaseField(dataType = DataType.UUID)
    @NonNull
    private UUID owner;
}
