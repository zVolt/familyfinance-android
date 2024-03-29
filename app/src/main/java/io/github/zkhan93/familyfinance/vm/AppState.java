package io.github.zkhan93.familyfinance.vm;

import androidx.annotation.DrawableRes;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppState extends ViewModel {
    private final MutableLiveData<Event<String>> fabAction = new MutableLiveData<>();
    private final MutableLiveData<String> fabActionId = new MutableLiveData<>();
    private final MutableLiveData<Integer> fabIcon = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fabShow = new MutableLiveData<>();

    public MutableLiveData<String> getFabActionID() {
        return fabActionId;
    }

    public MutableLiveData<Event<String>> getFabAction() {
        return fabAction;
    }

    public void onFabAction() {
        fabAction.setValue(new Event<>(fabActionId.getValue()));
    }

    public MutableLiveData<Integer> getFabIcon() {
        return fabIcon;
    }

    public void setFabIcon(@DrawableRes int icon) {
        fabIcon.setValue(icon);
    }

    public MutableLiveData<Boolean> getFabShow() {
        return fabShow;
    }

    public void setFabShow(boolean show) {
        fabShow.setValue(show);
    }

    public void enableFab(@DrawableRes int icon, String id){
        setFabShow(true);
        fabIcon.setValue(icon);
        fabActionId.setValue(id);
    }
    public void disableFab(){
        setFabShow(false);
        fabActionId.setValue(null);
    }
}
