package Maple.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * Data Transfer Object for RestCountries API response
 * Maps the country information returned by https://restcountries.com/v3.1/alpha/{code}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryDto {
    
    /** Map of demonyms by language code (e.g., "eng", "fra", "spa") */
    public Map<String, DemonymInfo> demonyms;

    /**
     * Inner class representing demonym information for a specific language
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DemonymInfo {
        /** Male form of the demonym (used as neutral, e.g., "American", "Spanish") */
        public String m; 
    }

    /**
     * Extracts the English demonym from the response
     * 
     * @return The English male demonym (e.g., "American", "Spanish") or null if not available
     */
    public String getDemonym() {
        if (demonyms != null && demonyms.containsKey("eng")) {
            DemonymInfo eng = demonyms.get("eng");
            return eng.m;
        }
        return null;
    }
}
