package requests.filestation.lists;

import exeptions.DsmListFolderException;
import requests.DsmAbstractRequest;
import requests.DsmAuth;
import requests.filestation.DsmRequestParameters;
import responses.filestation.lists.DsmGetInfoResponse;
import responses.Response;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DsmGetInfoRequest extends DsmAbstractRequest<DsmGetInfoResponse> {
    private List<String> targetPaths = new ArrayList<>();
    private List<DsmRequestParameters.Additional> additionals = new ArrayList<>();

    public DsmGetInfoRequest(DsmAuth auth) {
        super(auth);
        this.apiName = "SYNO.FileStation.List";
        this.version = 2;
        this.method = "getinfo";
        this.path = "webapi/entry.cgi";
    }

    @Override
    protected TypeReference getClassForMapper() {
        return new TypeReference<Response<DsmGetInfoResponse>>() {};
    }

    public DsmGetInfoRequest addTargetPath(String targetPath) {
        this.targetPaths.add(targetPath);
        return this;
    }

    /**
     * Optional. Additional requested file
     * information, separated by a
     * comma. When an additional
     * option is requested, responded
     * objects will be provided in the
     * specified additional option
     * @param additional additional
     * @return DsmListFolderRequest
     */
    public DsmGetInfoRequest addAdditionalInfo(DsmRequestParameters.Additional additional) {
        this.additionals.add(additional);
        return this;
    }

    /**
     * remove additional
     * @param additional additional
     * @return DsmListFolderRequest
     */
    public DsmGetInfoRequest removeAdditional(DsmRequestParameters.Additional additional) {
        this.additionals.remove(additional);
        return this;
    }

    @Override
    public Response<DsmGetInfoResponse> call() {
        if (targetPaths.isEmpty())
            throw new DsmListFolderException("the path list can not be empty");
        
        addParameter("path", "%5B" + targetPaths.stream().map(p -> "%22" + escape(p) + "%22").collect(Collectors.joining(",")) + "%5D");

        if(!additionals.isEmpty()) {
            addParameter("additional", additionals.stream().map(DsmRequestParameters.Additional::name).collect(Collectors.joining(",")));
        }

        return super.call();
    }
}
