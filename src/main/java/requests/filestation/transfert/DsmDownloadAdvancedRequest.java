package requests.filestation.transfert;

import com.fasterxml.jackson.core.type.TypeReference;
import exeptions.DsmDownloadException;
import exeptions.DsmListFolderException;
import requests.DsmAbstractRequest;
import requests.DsmAuth;
import requests.filestation.DsmRequestParameters;
import responses.filestation.transfert.DsmDownloadResponse;
import responses.Response;
import utils.DsmUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Download files/folders. If only one file is specified, the file content is responded. If more than one
 * file/folder is given, binary content in ZIP format which they are compressed to is responded.
 */
public class DsmDownloadAdvancedRequest extends DsmAbstractRequest<DsmDownloadResponse> {

    /**
     * One or more file/folder paths starting with a
     * shared folder to be downloaded, separated
     * by a commas. When more than one file
     * is to be downloaded, files/folders will be
     * compressed as a zip file.
     */
    private List<String> filePath = new ArrayList<String>();
    /**
     * Mode used to download files/folders, value
     * could be:
     * (1) open: try to trigger the application,
     * such as a web browser, to open it.
     * Content-Type of the HTTP header of
     * the response is set to MIME type
     * according to file extension.
     * (2) download: try to trigger the application,
     * such as a web browser, to download it.
     * Content-Type of the HTTP header of
     * response is set to application/octetstream and Content-Disposition of the
     * HTTP header of the response is set to
     * attachment.
     */
    private DsmRequestParameters.Mode mode = DsmRequestParameters.Mode.OPEN;
    
    private OutputStream output = null;

    /**
     * the destination where to save the downloaded file
     */
    private String destinationPath;
    public DsmDownloadAdvancedRequest(DsmAuth auth) {
        super(auth);
        this.apiName = "SYNO.FileStation.Download";
        this.version = 1;
        this.method = "download";
        this.path = "webapi/entry.cgi";
    }

    @Override
    protected TypeReference getClassForMapper() {
        return new TypeReference<Response<DsmDownloadResponse>>() {};
    }
    
    public DsmDownloadAdvancedRequest addFileToDownload(String filePath) {
        this.filePath.add(filePath);
        return this;
    }


    public DsmDownloadAdvancedRequest setMode(DsmRequestParameters.Mode mode) {
        this.mode = mode;
        return this;
    }

    public DsmDownloadAdvancedRequest setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
        return this;
    }
    
    public DsmDownloadAdvancedRequest setOutputStream(OutputStream output) {
        this.output = output;
        return this;
    }

    @Override
    public Response<DsmDownloadResponse> call() {
        if (this.filePath.isEmpty())
            throw new DsmDownloadException("You have to add a folder or file to download");

        addParameter("path", "%5B" + this.filePath.stream().map(p -> "%22" + escape(p) + "%22").collect(Collectors.joining(",")) + "%5D");
        addParameter("mode", this.mode.name());

        try {
            HttpURLConnection conn = handleRequest(build());
            
            Response<DsmDownloadResponse> response = new Response<>();
            
            if (this.output != null) {
                conn.getInputStream().transferTo(output);
            } else {
                File downloadedFile = DsmUtils.downloadFile(conn.getInputStream(), Optional.ofNullable(this.destinationPath).orElseThrow(() -> new DsmDownloadException("You have to set a destination path")));
            }

            response.setData(new DsmDownloadResponse(null));
            response.setSuccess(true);
            return response;
        } catch (IOException e) {
            throw new DsmDownloadException(e.toString());
        }
    }
}
