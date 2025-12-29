package com.example.addmission_update_project;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Activity_Web_Url_Loader extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView loadingText, urlText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Database_Favourite_Unique_Id dbHelper;
    private String currentUrl;
    private ImageView back, menu;
    private boolean isPdfFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_url_loader);

        // Initialize views
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        urlText = findViewById(R.id.url_text);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        back = findViewById(R.id.back);
        menu = findViewById(R.id.menu);

        // Initialize database helper
        dbHelper = new Database_Favourite_Unique_Id(this);

        // Check for direct URL first
        String directUrl = getIntent().getStringExtra("direct_url");
        if (directUrl != null && !directUrl.isEmpty()) {
            currentUrl = extractUrlFromString(directUrl);
            if (currentUrl != null && !currentUrl.isEmpty()) {
                urlText.setText(currentUrl);
                checkIfPdfUrl(currentUrl);
                configureWebView();
                setupClickListeners();
                setupSwipeRefresh();
                return;
            }
        }

        // Get uniqueId from intent
        String uniqueId = getIntent().getStringExtra("uniqueId");
        if (uniqueId == null || uniqueId.isEmpty()) {
            showErrorAndFinish("No website URL found");
            return;
        }

        // Fetch website URL from database
        currentUrl = getWebsiteUrlFromDatabase(uniqueId);
        if (currentUrl == null || currentUrl.isEmpty()) {
            showErrorAndFinish("Website URL not available");
            return;
        }

        checkIfPdfUrl(currentUrl);
        urlText.setText(currentUrl);
        configureWebView();
        setupClickListeners();
        setupSwipeRefresh();

        // Set progress bar color
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_theme_color_second),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void checkIfPdfUrl(String url) {
        isPdfFile = url != null && (url.endsWith(".pdf") || url.contains("drive.google.com/file/d/"));
    }

    private void setupClickListeners() {
        back.setOnClickListener(v -> finish());

        menu.setOnClickListener(v -> showPopupMenu());
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.app_theme_color_second)
        );
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(this, android.R.color.white)
        );

        // Disable swipe refresh for PDF files
        if (isPdfFile) {
            swipeRefreshLayout.setEnabled(false);
            return;
        }

        // For regular web pages, enable refresh only when at top
        webView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            swipeRefreshLayout.setEnabled(webView.getScrollY() == 0);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (webView != null) {
                webView.reload();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private String extractUrlFromString(String input) {
        if (input == null) return null;

        if (input.startsWith("http://") || input.startsWith("https://")) {
            return input;
        }

        if (input.contains("href=")) {
            return input.replaceAll(".*href=\"(.*?)\".*", "$1");
        }

        String urlPattern = "(https?:\\/\\/(?:www\\.|(?!www))[^\\s\\.]+\\.[^\\s]{2,}|www\\.[^\\s]+\\.[^\\s]{2,})";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(urlPattern);
        java.util.regex.Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String url = matcher.group();
            if (!url.startsWith("http")) {
                url = "https://" + url;
            }
            return url;
        }

        return null;
    }

    private String getWebsiteUrlFromDatabase(String uniqueId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String websiteUrl = "";

        Cursor cursor = db.query(
                "stored_university_info_data",
                new String[]{"website_url"},
                "unique_id = ?",
                new String[]{uniqueId},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            websiteUrl = cursor.getString(cursor.getColumnIndexOrThrow("website_url"));
            cursor.close();
        }
        db.close();

        if (websiteUrl.contains("href=")) {
            websiteUrl = websiteUrl.replaceAll(".*href=\"(.*?)\".*", "$1");
        }

        return websiteUrl;
    }

    private void configureWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                currentUrl = url;
                urlText.setText(url);
                checkIfPdfUrl(url);
                setupSwipeRefresh(); // Update swipe refresh setting if URL changes
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                if (view.getTitle() != null && !view.getTitle().isEmpty()) {
                    urlText.setText(view.getTitle());
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }
            }
        });

        webView.setDownloadListener((downloadUrl, userAgent, contentDisposition, mimetype, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setMimeType(mimetype);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(contentDisposition);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
        });

        webView.loadUrl(currentUrl);
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.webview_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_open_in_browser) {
                openInExternalBrowser();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void openInExternalBrowser() {
        if (currentUrl != null && !currentUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No URL available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}