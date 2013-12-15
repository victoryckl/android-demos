/*
bbs:pv:1000000,uv:300000
server:pv:200
cloud:pv:1000
*/

var cloudad_urls = [
'http://ad.doubleclick.net/click;h=v2%7C3EF5%7C0%7C0%7C%2a%7Ct;253507197;0-0;0;77325652;31-1%7C1;46665465%7C46681948%7C1;;%3fhttp://www.intel.com/cn/e5?dfaid=1&crtvid=%ecid!;'
, 'http://ad.doubleclick.net/clk;254998503;77325652;v?http://www.intel.com/zh_CN/itcenter/flv/CNNewV33/index.htm?dfaid=1&crtvid=%ecid!;'
, 'http://ad.doubleclick.net/clk;254998506;77325652;y?http://www.intel.com/zh_CN/itcenter/pdf/CN_w25_Best_Practices_for_Building_an_Enterprise_Private_Cloud.pdf?dfaid=1&crtvid=%ecid!;'
, 'http://ad.doubleclick.net/clk;254998509;77325652;b?http://www.intel.com/zh_CN/itcenter/pdf/CN_b16_Intel_Xeon_E5-1600_2600_Product_Brief_CS_Public.pdf?dfaid=1&crtvid=%ecid!;'
];

function cloudad_show() {
    var rd = Math.random();
    if (rd < 0.3) {
        var ad_url = 'http://ad.csdn.net/show.aspx?src=';
        var ad_src = '';
        if (rd < 0.1) {
            ad_src = 'http://ad.csdn.net/adsrc/server.flash-ad-text-302x190.swf';
        } else {
            ad_src = 'http://ad.csdn.net/adsrc/cloud.flash-ad-text-210x160.swf';
        }
        ad_url += encodeURIComponent(ad_src);

        cloudad_doRequest(ad_url, false);

        var view_url = 'http://event.blog.csdn.net/cloudad/view.aspx';
        view_url += '?view=1&adtype=intel_test&adurl=' + encodeURIComponent(ad_src);
        cloudad_doRequest(view_url, false);

        cloudad_clickad();
    }
}
function cloudad_clickad() {
    var rd = Math.random();
    if (rd < 0.006) {
        var ad_url = cloudad_urls[parseInt(Math.random() * 100 % cloudad_urls.length)];
        cloudad_doRequest(ad_url, false);

        var click_url = 'http://event.blog.csdn.net/cloudad/click.aspx';
        click_url += '?click=1&adtype=intel_test&adurl=' + encodeURIComponent(ad_url);
        cloudad_doRequest(click_url, false);
    }
}
function cloudad_doRequest(url, useFrm) {
    var e = null;
    if (useFrm) {
        e = document.createElement("iframe");
    } else {
        e = document.createElement("img");
    }
    if (url.indexOf('?') > 0) url += '&r_m=';
    else url += '?r_m=';
    url += new Date().getMilliseconds();
    e.style.width = "1px";
    e.style.height = "1px";
    e.style.position = "absolute";
    e.style.visibility = "hidden";
    e.src = url;
    document.body.appendChild(e);
}

setTimeout(function () {
    cloudad_show();
}, 1000);
