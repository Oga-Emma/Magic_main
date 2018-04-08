package ai.magicmirror.magicmirror.models;

public class CountryDTO {
        public final String name;
        public final String callCode;

        public CountryDTO(String name, String callCode) {
            this.name = name;
            this.callCode = callCode;
        }

        public String getName() {
            return name;
        }

        public String getCallCode() {
            return callCode;
        }
}
