package Maple.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryDto {
    
    public Map<String, DemonymInfo> demonyms;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DemonymInfo {
        public String f; // female
        public String m; // male
    }

    public String getDemonym() {
        if (demonyms != null) {
            if (demonyms.containsKey("spa")) {
                DemonymInfo spa = demonyms.get("spa");
                return spa.m != null ? spa.m : spa.f;
            }
            if (demonyms.containsKey("eng")) {
                DemonymInfo eng = demonyms.get("eng");
                return eng.m != null ? eng.m : eng.f;
            }
        }
        return null;
    }
}
