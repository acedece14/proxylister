package by.katz;

import java.util.List;

public interface ICallback {

    void onComplete(List<ProxyItem> goodProxies);

    void onGet(int count);

    void onError(String error);
}
