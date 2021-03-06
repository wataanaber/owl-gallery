package jp.co.andfactory.materialgallery.presentation.gallery

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.rxkotlin.subscribeBy
import jp.co.andfactory.materialgallery.R
import jp.co.andfactory.materialgallery._extension.contentViewBinding
import jp.co.andfactory.materialgallery._extension.gone
import jp.co.andfactory.materialgallery._extension.visible
import jp.co.andfactory.materialgallery.databinding.ActivityGalleryBinding
import jp.co.andfactory.materialgallery.domain.model.MaterialPhoto
import jp.co.andfactory.materialgallery.presentation.common.adapter.PhotosAdapter
import jp.co.andfactory.materialgallery.util.GridRecyclerViewScrolledToEndSubject
import javax.inject.Inject

class GalleryActivity : AppCompatActivity(), GalleryContract.View, LifecycleOwner, HasSupportFragmentInjector {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, GalleryActivity::class.java)
        }
    }

    @Inject
    lateinit var presenter: GalleryContract.Presenter
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Fragment>

    private val binding: ActivityGalleryBinding by contentViewBinding(R.layout.activity_gallery)
    private var adapter: PhotosAdapter? = null

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        lifecycle.addObserver(presenter)

        binding.apply {
            recyclerView.layoutManager = GridLayoutManager(this@GalleryActivity, 3)
        }
        presenter.fetchPhotos(true)

        val endToSubject = GridRecyclerViewScrolledToEndSubject(binding.recyclerView)
        endToSubject.connection().subscribeBy(
                onNext = {
                    presenter.fetchPhotos(false)
                },
                onError = {
                    //noting to do
                }
        )
    }

    override fun showPhotos(list: List<MaterialPhoto>, isRefresh: Boolean) {
        Log.d("gallery", list.toString())
        if (isRefresh) {
            adapter = PhotosAdapter(this, list.toMutableList(), object : PhotosAdapter.OnItemClickListener {
                override fun onItemClick(item: MaterialPhoto) {
                    showPhotoDialog(item)
                }
            })
            binding.recyclerView.adapter = adapter
        } else {
            adapter?.addItems(list)
        }

    }

    override fun showPhotoDialog(photo: MaterialPhoto) {
        PhotoDialogFragment.newInstance(photo.id)
                .show(supportFragmentManager, "GalleryActivity.showPhotoDialog")
    }

    override fun showProgress() = binding.progressBar.visible()

    override fun hideProgress() = binding.progressBar.gone()

    override fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE).show()
    }
}
