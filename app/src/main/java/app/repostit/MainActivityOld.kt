package app.repostit

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.repostit.entity.ImageData
import app.repostit.service.FloatingWidgetService
import app.repostit.ui.adapter.CustomPagerAdapter
import app.repostit.service.ClipboardService
import app.repostit.utils.Extras
import app.repostit.utils.ImageDownloadTask
import app.repostit.utils.Util
import app.repostit.utils.Util.shareImageTask
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback


class MainActivityOld : AppCompatActivity() {


    lateinit var context: Context
    var imgData: ImageData? = null
    lateinit var imagedata: ImageData

    lateinit var customPagerAdapter: CustomPagerAdapter;

    private val DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        context = this

        MobileAds.initialize(this, getString(R.string.ad_mob_appId))

        startIntroActivity()

        askForPermissions()

        navigation.visibility = View.GONE

        customPagerAdapter = CustomPagerAdapter(supportFragmentManager, this)
        viewpager.adapter = customPagerAdapter
        viewpager?.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 0)
                    showNavigation(true)
                else
                    showNavigation(false)
            }

            override fun onPageSelected(position: Int) {

            }

        })

        tabs.setupWithViewPager(viewpager)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        var rawdata: String? = null
        intent?.extras?.run {
            rawdata = getString(Extras.IMAGE_JSON)
            handleIntent(rawdata)
        }

        appbar.visibility = View.GONE

        if (!isServiceRunning(ClipboardService::class.java)) {
            startService(Intent(this, ClipboardService::class.java))
        }

    }


    fun HideTab(flag: Boolean) {
        if (flag)
            appbar.visibility = View.GONE
        else
            appbar.visibility = View.VISIBLE
    }

    fun askForPermissions() {

        Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, object : PermissionCallback {
            override fun permissionGranted() {
            }

            override fun permissionRefused() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun startIntroActivity() {

        if (Util.isFreshLaunch(this)) {
            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    fun showNavigation(flag: Boolean) {
        if (flag && imgData != null)
            navigation.visibility = View.VISIBLE
        else
            navigation.visibility = View.GONE
    }

    fun refreshAdapter(rawdata: String) {
        if (!rawdata.equals("")) {
            navigation.visibility = View.VISIBLE
            imgData = parseJsonAndGetUrl(rawdata)
            customPagerAdapter.handleIntent(imgData)
            customPagerAdapter.notifyDataSetChanged()
        }
    }

    fun handleIntent(rawdata: String?) {
        if (rawdata != null) {
            refreshAdapter(rawdata)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.getItemId()) {

            R.id.menu_overlay -> openSettingActivity()
            R.id.menu_heart -> Util.openRateApppage(this);
            R.id.menu_instagram -> Util.openInstagram(this)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar, menu)
        return true
    }


    fun saveImage(context: Context) {
        Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, object : PermissionCallback {
            override fun permissionGranted() {
                ImageDownloadTask(this@MainActivityOld).execute(imagedata.url, imagedata.name, imagedata.video_url, imagedata.is_Video.toString())
            }

            override fun permissionRefused() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    fun parseJsonAndGetUrl(json: String): ImageData? {

        try {
            val jsonObj = JSONObject(json)
            val graphql = jsonObj.getJSONObject("graphql")
            val shortcode_media = graphql.optJSONObject("shortcode_media")

            val url = shortcode_media.getString("display_url")
            val imageName = shortcode_media.getString("id")
            val owner = shortcode_media.optJSONObject("owner")
            val dimensions = shortcode_media.optJSONObject("dimensions")
            val width = dimensions.getInt("width")
            val height = dimensions.getInt("height")

            val is_video = shortcode_media.getBoolean("is_video")
            var video_url = ""
            if (is_video)
                video_url = shortcode_media.getString("video_url")

            val profileUrl = owner.getString("profile_pic_url")
            val profileName = owner.getString("username")
            val profileid = owner.getString("id")


            val edge_media_to_caption = shortcode_media.optJSONObject("edge_media_to_caption")
            val edges = edge_media_to_caption.optJSONArray("edges")
            var desc = ""
            if (edges.length() > 0) {
                val zero = edges.get(0) as JSONObject
                val node = zero.optJSONObject("node")
                desc = node.getString("text")
            }

            val likes = shortcode_media.optJSONObject("edge_media_preview_like").getInt("count")

            imagedata = ImageData(imageName, desc, url, is_video, video_url, width, height, profileid, profileName, profileUrl, likes)

            /*if (is_video)
                return "Video:" + video_url*/

            return imagedata


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null

    }

    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter();
        intentFilter.addAction(ClipboardService.MY_ACTION);
        registerReceiver(broadCastReceiver, intentFilter);

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadCastReceiver)

    }

    override fun onResume() {
        super.onResume()
        object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this@MainActivityOld)) {
                    startFloatingWidgetService()
                    this.cancel()
                }
            }

            override fun onFinish() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this@MainActivityOld)) {
                    startFloatingWidgetService()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("RepostIt", "Service onDestroy now")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        //stopService(Intent(this@MainActivityOld, ClipboardService::class.java))
        //stopService(Intent(this@MainActivityOld, FloatingWidgetService::class.java))
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            refreshAdapter(intent?.extras?.getString(Extras.LINK_RAW_DATA)!!)
        }
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun openSettingActivity() {

        val builder = AlertDialog.Builder(this@MainActivityOld)
        builder.setTitle(R.string.alert_dialog_title)
        builder.setMessage(R.string.alert_dialog_msg)
        builder.setPositiveButton("Open Setting") { dialog, which ->
            createFloatingWidget()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->

        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun createFloatingWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE)
        }
    }

    private fun startFloatingWidgetService() {
        startService(Intent(this@MainActivityOld, FloatingWidgetService::class.java))
    }

    private val mOnNavigationItemSelectedListener = com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_download -> {
                saveImage(this)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_share -> {

                shareImageTask(this, imagedata.url, imagedata.name, imagedata.description, imagedata.is_Video, imagedata.video_url)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_repost -> {
                if (Util.isPackageInstalled("com.instagram.android", packageManager))
                    Util.postOnInstagram(this, imagedata.url, imagedata.name, imagedata.description, imagedata.is_Video, imagedata.video_url)
                else {
                    Toast.makeText(this, "Instagram not installed", Toast.LENGTH_SHORT).show()
                }

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


}
