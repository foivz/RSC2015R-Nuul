package hr.nullteam.rsc.ui.presenter.profile;

import javax.inject.Inject;

import hr.nullteam.rsc.business.api.UserApi;
import hr.nullteam.rsc.business.api.model.User;
import hr.nullteam.rsc.ui.fragment.profile.ProfileDetailsFragment;
import hr.nullteam.rsc.ui.presenter.BusPresenter;
import hr.nullteam.rsc.util.PreferenceUtils;
import hr.nullteam.rsc.util.ToastUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileDetailsFragmentPresenter extends BusPresenter<ProfileDetailsFragment> {

    @Inject
    UserApi userApi;

    @Inject
    PreferenceUtils preferenceUtils;

    @Inject
    ToastUtils toastUtils;

    private Subscription playerSubscription;

    public void onUserId(long userId) {
        if(userId == User.UNDEFNED_ID) {
            setViewData(preferenceUtils.getUser());
        } else {
            playerSubscription = userApi.getPlayerWithId(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onPlayerSuccess, this::onPlayerError, this::onPlayerCompletion);
            add(playerSubscription);
        }
    }

    private void onPlayerSuccess(User user) {
        setViewData(user);
    }

    private void onPlayerError(Throwable throwable) {
        remove(playerSubscription);
        throwable.printStackTrace();
        toastUtils.showToast("Error fetching user data");
        setViewData(User.EMPTY);
    }

    private void onPlayerCompletion() {
        remove(playerSubscription);
    }

    public void setViewData(User user) {
        ProfileDetailsFragment fragment = getView();
        if(fragment == null) {
            return;
        }
        fragment.setUserData(user);
    }

}
