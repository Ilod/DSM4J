package responses.filestation.lists;

import responses.filestation.DsmResponseFields;

import java.util.List;

public class DsmGetInfoResponse {
    private List<DsmResponseFields.Files> files;

    public List<DsmResponseFields.Files> getFiles() {
        return files;
    }
}
