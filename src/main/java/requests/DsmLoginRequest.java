package requests;

import com.fasterxml.jackson.core.type.TypeReference;
import responses.DsmLoginResponse;
import responses.Response;

public class DsmLoginRequest extends DsmAbstractRequest<DsmLoginResponse> {
    public DsmLoginRequest(DsmAuth auth) {
        super(auth);
        this.apiName = "SYNO.API.Auth";
        this.version = 3;
        this.method = "login";
        this.path = "webapi/auth.cgi";

        addParameter("account", auth.getUserName());
        addParameter("passwd", auth.getPassword());
    }

    @Override
    protected TypeReference getClassForMapper() {
        return new TypeReference<Response<DsmLoginResponse>>() {};
    }
}
