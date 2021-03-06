package jp.co.andfactory.materialgallery.presentation.gallery

import android.app.Activity
import android.arch.lifecycle.LifecycleObserver
import jp.co.andfactory.materialgallery.domain.model.MaterialPhoto

interface GalleryContract {

    interface View {
        fun showPhotos(list: List<MaterialPhoto>, isRefresh: Boolean)
        fun showPhotoDialog(photo: MaterialPhoto)
        fun showProgress()
        fun hideProgress()
        fun showError(message: String)
    }

    interface Presenter: LifecycleObserver {
        fun onResume() // base
        fun onPause()  // base
        fun onDestroy() //base

        fun fetchPhotos(isRefresh: Boolean = false)
        fun onClickItem(photo: MaterialPhoto)
    }

    interface Router {
        fun openDetail(activity: Activity, photo: MaterialPhoto)
    }
}
