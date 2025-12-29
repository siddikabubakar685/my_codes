package com.example.addmission_update_project;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Activity_Faculty_Subjects_details extends AppCompatActivity {

    private TextView titleBar;
    private ImageView backButton;
    private ImageView noticeDetailImage;
    private ProgressBar loadingProgressBar;
    private WebView subjectDetailsWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_subjects_details);

        // Find views
        titleBar = findViewById(R.id.titleBar);
        backButton = findViewById(R.id.back);
        noticeDetailImage = findViewById(R.id.notice_detail_image);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        subjectDetailsWebView = findViewById(R.id.subject_details_webview);

        // Get data from Intent
        String subjectName = getIntent().getStringExtra("SUBJECT_NAME");
        String subjectDescription = getIntent().getStringExtra("SUBJECT_DETAILS");
        String subjectImageUrl = getIntent().getStringExtra("SUBJECT_IMAGE_URL");

        // Set title
        if (subjectName != null && !subjectName.isEmpty()) {
            titleBar.setText(subjectName);
        } else {
            titleBar.setText("Subject Details");
        }

        // Set back button action
        backButton.setOnClickListener(v -> finish());

        // Configure WebView for HTML content
        setupWebView();

        // Load subject details in WebView
        if (subjectDescription != null && !subjectDescription.isEmpty()) {
            String cleanedHtml = cleanHtmlContent(subjectDescription);
            loadHtmlContent(cleanedHtml);
        } else {
            loadHtmlContent("<div style='text-align:center; color:#666; padding:40px; font-size:16px;'>No description available</div>");
        }

        // Load subject image with Picasso
        loadSubjectImage(subjectImageUrl);
    }

    private void setupWebView() {
        WebSettings webSettings = subjectDetailsWebView.getSettings();

        // Enable basic settings
        webSettings.setJavaScriptEnabled(true); // Enable for better compatibility
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);

        // Enable DOM storage for better HTML5 support
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);

        // Set background
        subjectDetailsWebView.setBackgroundColor(0x00000000);

        // Set WebView client
        subjectDetailsWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                loadHtmlContent("<div style='text-align:center; color:#ff0000; padding:40px; font-size:16px;'>Failed to load content</div>");
            }
        });
    }

    private String cleanHtmlContent(String htmlContent) {
        if (htmlContent == null) return "";

        String cleaned = htmlContent;

        // Remove empty paragraphs with only <br> tags
        cleaned = cleaned.replaceAll("<p><br></p>", "");
        cleaned = cleaned.replaceAll("<p><br/></p>", "");
        cleaned = cleaned.replaceAll("<p>&nbsp;</p>", "");
        cleaned = cleaned.replaceAll("<p>\\s*</p>", "");

        // Remove multiple consecutive <br> tags
        cleaned = cleaned.replaceAll("(<br>\\s*){2,}", "<br>");
        cleaned = cleaned.replaceAll("(<br/>\\s*){2,}", "<br/>");

        // Remove empty blockquotes
        cleaned = cleaned.replaceAll("<blockquote><br></blockquote>", "");
        cleaned = cleaned.replaceAll("<blockquote>\\s*</blockquote>", "");

        // Fix target="_blank" links for Android
        cleaned = cleaned.replaceAll("target=\"_blank\"", "");

        // Ensure proper HTML structure
        if (!cleaned.contains("<html")) {
            cleaned = "<div>" + cleaned + "</div>";
        }

        return cleaned;
    }

    private void loadHtmlContent(String htmlContent) {
        // Create styled HTML with proper CSS
        String styledHtml = createStyledHtml(htmlContent);

        // Load the HTML content
        subjectDetailsWebView.loadDataWithBaseURL(
                null,
                styledHtml,
                "text/html",
                "UTF-8",
                null
        );
    }

    private String createStyledHtml(String content) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "<style>\n" +
                "body { \n" +
                "    font-family: 'Roboto', 'Noto Sans Bengali', 'Helvetica', 'Arial', sans-serif; \n" +
                "    font-size: 16px; \n" +
                "    line-height: 1.6; \n" +
                "    color: #333333; \n" +
                "    background-color: #ffffff; \n" +
                "    padding: 0px; \n" +
                "    margin: 0px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "h1 { \n" +
                "    font-size: 22px; \n" +
                "    font-weight: bold; \n" +
                "    margin: 20px 0 15px 0; \n" +
                "    color: #2c3e50; \n" +
                "    line-height: 1.3;\n" +
                "}\n" +
                "h2 { \n" +
                "    font-size: 20px; \n" +
                "    font-weight: bold; \n" +
                "    margin: 18px 0 12px 0; \n" +
                "    color: #34495e; \n" +
                "    line-height: 1.3;\n" +
                "}\n" +
                "h3 { \n" +
                "    font-size: 18px; \n" +
                "    font-weight: bold; \n" +
                "    margin: 16px 0 10px 0; \n" +
                "    color: #34495e; \n" +
                "    line-height: 1.3;\n" +
                "}\n" +
                "h4 { \n" +
                "    font-size: 16px; \n" +
                "    font-weight: bold; \n" +
                "    margin: 14px 0 8px 0; \n" +
                "    color: #34495e; \n" +
                "}\n" +
                "h5 { \n" +
                "    font-size: 14px; \n" +
                "    font-weight: bold; \n" +
                "    margin: 12px 0 6px 0; \n" +
                "    color: #34495e; \n" +
                "}\n" +
                "h6 { \n" +
                "    font-size: 12px; \n" +
                "    font-weight: bold; \n" +
                "    margin: 10px 0 4px 0; \n" +
                "    color: #34495e; \n" +
                "}\n" +
                "p { \n" +
                "    margin: 12px 0; \n" +
                "    text-align: justify; \n" +
                "    line-height: 1.6;\n" +
                "}\n" +
                "ul, ol { \n" +
                "    margin: 12px 0 12px 20px; \n" +
                "}\n" +
                "li { \n" +
                "    margin: 8px 0; \n" +
                "    line-height: 1.5; \n" +
                "}\n" +
                "strong, b { \n" +
                "    font-weight: bold; \n" +
                "}\n" +
                "em, i { \n" +
                "    font-style: italic; \n" +
                "}\n" +
                "u { \n" +
                "    text-decoration: underline; \n" +
                "}\n" +
                "a { \n" +
                "    color: #2563eb; \n" +
                "    text-decoration: underline; \n" +
                "}\n" +
                "blockquote { \n" +
                "    border-left: 4px solid #3498db; \n" +
                "    padding: 12px 16px; \n" +
                "    margin: 16px 0; \n" +
                "    background-color: #f8f9fa; \n" +
                "    font-style: italic; \n" +
                "    color: #555; \n" +
                "    line-height: 1.5;\n" +
                "}\n" +
                "span { \n" +
                "    display: inline;\n" +
                "}\n" +
                "img { \n" +
                "    max-width: 100%; \n" +
                "    height: auto; \n" +
                "    display: block; \n" +
                "    margin: 12px auto; \n" +
                "}\n" +
                "table { \n" +
                "    width: 100%; \n" +
                "    border-collapse: collapse; \n" +
                "    margin: 16px 0; \n" +
                "}\n" +
                "table, th, td { \n" +
                "    border: 1px solid #ddd; \n" +
                "}\n" +
                "th, td { \n" +
                "    padding: 10px 12px; \n" +
                "    text-align: left; \n" +
                "}\n" +
                "th { \n" +
                "    background-color: #f2f2f2; \n" +
                "    font-weight: bold; \n" +
                "}\n" +
                ".ql-align-center { \n" +
                "    text-align: center; \n" +
                "}\n" +
                ".ql-align-right { \n" +
                "    text-align: right; \n" +
                "}\n" +
                ".ql-align-justify { \n" +
                "    text-align: justify; \n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                content +
                "</body>\n" +
                "</html>";
    }

    private void loadSubjectImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            noticeDetailImage.setVisibility(View.GONE);

            Picasso.get()
                    .load(imageUrl)
                    .into(noticeDetailImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingProgressBar.setVisibility(View.GONE);
                            noticeDetailImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            loadingProgressBar.setVisibility(View.GONE);
                            noticeDetailImage.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            noticeDetailImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (subjectDetailsWebView != null) {
            subjectDetailsWebView.destroy();
        }
        super.onDestroy();
    }
}