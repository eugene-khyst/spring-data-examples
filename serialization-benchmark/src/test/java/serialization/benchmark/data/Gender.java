package serialization.benchmark.data;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author EKhyst
 */
public enum Gender {
    
    MALE("male"), FEMALE("female");
    
    private final String gender;
    
    private Gender(String gender) {
        this.gender = gender;
    }

    @JsonValue
    public String getGender() {
        return gender;
    }
}
