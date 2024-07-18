package query.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Schema {
    private final List<Field> fields;

    public Schema(List<Field> fields) {
        this.fields = fields;
    }

    public Schema select(List<String> names) {
        List<Field> selectedFields = new ArrayList<>();
        for (String name : names) {
            List<Field> matchingFields = fields.stream()
                    .filter(f -> f.getName().equals(name))
                    .collect(Collectors.toList());
            if (matchingFields.size() == 1) {
                selectedFields.add(matchingFields.get(0));
            } else {
                throw new IllegalArgumentException();
            }
        }
        return new Schema(selectedFields);
    }

    public List<Field> getFields() {
        return fields;
    }
}