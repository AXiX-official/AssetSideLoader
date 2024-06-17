package top.axix.assetsideloader

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectedAppsAdapter(private val apps: List<AppInfo>) : RecyclerView.Adapter<SelectedAppsAdapter.ViewHolder>() {

    private var filteredIndices: List<Int> = apps.indices.filter { apps[it].isSelected }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.app_name)
        val appPackageName: TextView = view.findViewById(R.id.app_package_name)
        val enable: Switch = view.findViewById(R.id.enable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.work_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[filteredIndices[position]]
        holder.itemView.visibility = View.VISIBLE
        holder.appName.text = app.appName
        holder.appPackageName.text = app.packageName
        holder.enable.isChecked = app.isEnabled
        holder.enable.text = if (app.isEnabled) "Enabled" else "Disabled"
        holder.enable.setOnCheckedChangeListener { _, isChecked ->
            app.isEnabled = isChecked
            if (isChecked) {
                holder.enable.text = "Enabled"
            } else {
                holder.enable.text = "Disabled"
            }
        }
        val clickListener = View.OnClickListener {
            val context = it.context
            val intent = Intent(context, AppInfoActivity::class.java)
            intent.putExtra("position", filteredIndices[position])
            if (context is Activity) {
                context.startActivityForResult(intent, 2)
            } else {
                Log.e("SelectedAppsAdapter", "Context is not an activity")
                context.startActivity(intent)
            }
        }
        holder.appName.setOnClickListener(clickListener)
        holder.appPackageName.setOnClickListener(clickListener)
    }

    override fun getItemCount() = filteredIndices.size

    fun updateApps() {
        filteredIndices = apps.indices.filter { apps[it].isSelected }
        notifyDataSetChanged()
    }
}