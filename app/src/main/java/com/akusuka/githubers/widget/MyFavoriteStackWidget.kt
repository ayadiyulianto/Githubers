package com.akusuka.githubers.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.akusuka.githubers.DetailActivity
import com.akusuka.githubers.FavoriteActivity
import com.akusuka.githubers.R


class MyFavoriteStackWidget : AppWidgetProvider() {

    companion object {
        fun sendRefreshBroadcast(context: Context) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.component = ComponentName(context, MyFavoriteStackWidget::class.java)
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // update all widget
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val intent = Intent(context, StackWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()
        val views = RemoteViews(context.packageName, R.layout.widget_my_favorite_stack)
        views.setRemoteAdapter(R.id.stack_view, intent)
        views.setEmptyView(R.id.stack_view, R.id.empty_view)

        // onclick title widget
        val titleIntent = Intent(context, FavoriteActivity::class.java)
        val titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, 0)
        views.setOnClickPendingIntent(R.id.banner_text, titlePendingIntent)

        // onclick item info
        val clickIntentTemplate = Intent(context, DetailActivity::class.java)
        val clickPendingIntentTemplate: PendingIntent? = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(clickIntentTemplate)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setPendingIntentTemplate(R.id.stack_view, clickPendingIntentTemplate)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            // refresh all widgets
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, MyFavoriteStackWidget::class.java)
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.stack_view)
        }
    }

}