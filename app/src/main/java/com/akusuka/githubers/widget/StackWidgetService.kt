package com.akusuka.githubers.widget

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.akusuka.githubers.DetailActivity
import com.akusuka.githubers.model.Users
import com.akusuka.githubers.R
import com.bumptech.glide.Glide


class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StackRemoteViewsFactory(this.applicationContext)
}


internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val CONTENT_URI: Uri = Uri.Builder().scheme("content")
        .authority("com.akusuka.githubers.provider")
        .appendPath("favorite")
        .build()
    private var mCursor: Cursor? = null

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        mCursor?.close()
        val identityToken: Long = Binder.clearCallingIdentity()
        mCursor = mContext.contentResolver?.query(CONTENT_URI, null, null, null, null)
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {
        mCursor?.close()
    }

    override fun getCount(): Int = mCursor?.count ?: 0

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION || mCursor == null || !mCursor!!.moveToPosition(position)) {
            return null
        }

        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setTextViewText(R.id.tv_item_widget, mCursor!!.getString(0))
        try {
            val bitmap: Bitmap = Glide.with(mContext)
                .asBitmap()
                .load(mCursor!!.getString(1))
                .submit(64, 64)
                .get()
            rv.setImageViewBitmap(R.id.img_item_widget, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // fill selected user to pending intent open detailactivity
        val users = Users(mCursor!!.getString(0))
        val extras = bundleOf(DetailActivity.EXTRA_USER to users)
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.btn_item_widget, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}