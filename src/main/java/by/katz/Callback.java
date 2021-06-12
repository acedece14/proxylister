package by.katz;

import java.util.List;

public interface Callback {

    void onComplete(List<ProxyItem> goodProxies);

    void onGet(int count);

    void onError(String error);
}
