package ubergesundheit.owrds;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceClickListener {

	private SharedPreferences pref;

	private Resources r;

	private Preference about;
	private Preference defaultValue;

	
	@SuppressWarnings("deprecation")
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayShowHomeEnabled(false);
		}

		r = getResources();

		pref = PreferenceManager.getDefaultSharedPreferences(this);

		about = findPreference(r.getString(R.string.const_setting_about));
		defaultValue = findPreference(r
				.getString(R.string.const_setting_default_value));
		about.setSummary(getVersionDate());
		about.setOnPreferenceClickListener(this);
		defaultValue.setOnPreferenceClickListener(this);

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.equals(defaultValue)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.str_dialog_reset_appearance_title)
					.setMessage(R.string.str_dialog_reset_appearance_body)
					.setPositiveButton(android.R.string.yes,
							new OnClickListener() {

								@SuppressWarnings("deprecation")
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									SharedPreferences.Editor prefEditor = pref
											.edit();
									prefEditor.putString(
											r.getString(R.string.const_setting_font),
											"Junction.otf");
									prefEditor.putInt(
											r.getString(R.string.const_setting_font_color),
											Color.WHITE);
									prefEditor.putInt(
											r.getString(R.string.const_setting_bg_color),
											Color.BLACK);
									prefEditor.putBoolean(r.getString(R.string.const_setting_draw_indicator), true);
									((CheckBoxPreference) findPreference(r.getString(R.string.const_setting_draw_indicator))).setChecked(true);
									prefEditor.commit();
									onContentChanged();
								}
							}).setNegativeButton(android.R.string.no, null)
					.show();

		} else if (preference.equals(about)) {
			startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
		}
		return true;
	}

	// Writes the date of the current version
	private String getVersionDate() {
		String out = "Version ";
		try {
			out += this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName
					+ " (";
		} catch (NameNotFoundException e) {
		}
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			long time = ze.getTime();
			out += SimpleDateFormat.getInstance().format(
					new java.util.Date(time))
					+ ")";

		} catch (Exception e) {
		}

		return out;
	}


}
