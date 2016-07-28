/**
 * @author LuYongXing
 * @date 2015.01.12
 * @filename API_Manger.java
 */

package com.camerarental.crc.utils;

public class Constant {

    public static final int REGISTER_TOKEN = 999;
    public static final int VERSION_CHECK = 1000;
    public static final int FILTER_DOWNLOAD = 1001;
    public static final int DATA_DOWNLOAD = 1002;

    public static final String REGISTER_DEVICE_URL = "http://app.camerarental.biz/api/crc/registerDevice.php";

    public static final String EQUIPMENT_VERSION_URL = "http://crc-sg.appspot.com/public/v1/mversion";
    public static final String EQUIPMENT_FILTER_URL = "http://app.camerarental.biz/data/menu.txt";
    public static final String EQUIPMENT_DOWNLOAD_URL = "http://crc-sg.appspot.com/public/v1/equipment?full=yes";
    public static final String EQUIPMENT_PROMOTION_URL = "http://camerarental.biz/app/promo.html";

    public static final String GEOCODING_SERVICE_API = "http://maps.google.com/maps/api/geocode/json?address=%s&sensor=false";

    // Filter param keys
    public static final String kFilterIDKey = "kFilterIDKey";
    public static final String kFilterNameKey = "kFilterNameKey";
    public static final String kFilterDisplayNameKey = "kFilterDisplayNameKey";
    public static final String kFilterSubFilterKey = "kFilterSubFilterKey";

    // Equipment param keys
    public static final String kEquipmentIDKey = "kEquipmentIDKey";
    public static final String kEquipmentInformationKey = "kEquipmentInfomationKey";
    public static final String kEquipmentCostKey = "kEquipmentCostKey";
    public static final String kEquipmentImageURLKey = "kEquipmentImageURLKey";
    public static final String kEquipmentKindKey = "kEquipmentKindKey";
    public static final String kEquipmentCountKey = "kEquipmentCountKey";
    public static final String kEquipmentStateKey = "kEquipmentStateKey";

    public static final String kUserNameKey = "UserNameKey";
    public static final String kUserContactNumKey = "UserConactNumKey";
    public static final String kUserNRICKey = "UserNRICKey";
    public static final String kUserMailKey= "UserMaikKey";

    public static final int kPortNumForMail = 465;
    public static final String kHostNameForMail = "smtp.gmail.com";
    public static final String kUserNameForMail = "app@camerarental.biz";
    public static final String kPasswordForMail = "crcapp888";
    public static final String kFolderNameForMail = "INBOX";

    public static final String kEMailUserNameForCRC = "CRC";
    public static final String kEMailAddressForCRC = "info@camerarental.biz";
    public static final String kContactNumForCRC = "96504158";

    public static final String kMsgHeaderForMail = "Thank you for sending in your order via our mobile app. Kindly note that your booking HAS NOT BEEN CONFIRMED. We will be processing your order shortly, and will send you a confirmation emailalong with booking details once your booking has been confirmed. If you have any enquiries or need to change your booking, please feel free to contact us at ";

    public static final String USER_NAME = "-user_name-";
    public static final String CONTACT_NUM = "-contact_number-";
    public static final String USER_NRIC = "-user_nric-";
    public static final String DATE = "-date-";
    public static final String EQUIPMENT_LIST = "-equipment_list-";
    public static final String COMMENT = "-comment-";

    public static final String BEGIN_DATE = "-begin_date-";
    public static final String END_DATE = "-end_date-";
    public static final String EQUIPMENT = "-equipment-";

    public static final String EQUIPMENT_TEMPLATE = "<span class=\"s2\">-equipment-</span><br>";

    public static final String SINGLE_DATE_TEMPLATE = "<span class=\"s2\">-begin_date-</span><br><br>";

    public static final String DOUBLE_DATE_TEMPLATE = "<span class=\"s2\">-begin_date- - -end_date-</span><br><br>";

    public static final String MAIL_TEMPLATE =
            "<html>" +
            "<head>" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
            "<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">" +
            "<title></title>" +
            "<style type=\"text/css\">" +
            "br { display:block; margin-top:0.0px; marin-bottom:0.0px; line-height:4.0px; } " +
            "span.s1 {font: 14.0px Helvetica; font-kerning: none; -webkit-text-stroke: 0px #000000} " +
            "span.s2 {font: 14.0px Helvetica; font-variant-ligatures: no-common-ligatures} " +
            "span.s3 {font: 14.0px Helvetica; font-kerning: none} " +
            "span.s4 {font: 14.0px Helvetica; text-decoration: underline ; font-kerning: none; color: #4787ff; -webkit-text-stroke: 0px #0068cf} " +
            "span.s5 {font: 14.0px Helvetica; font-variant-ligatures: no-common-ligatures; -webkit-text-stroke: 0px #000000} " +
            "</style>" +
            "</head>" +
            "<body>" +
            "<span class=\"s1\">Dear </span><span class=\"s2\">-user_name-</span><span class=\"s1\">,</span><br><br>" +
            "<span class=\"s3\">Thank you for your sending your rental order. </span><br><br>" +
            "<span class=\"s3\">We will be processing your order shortly, and will send you a confirmation email along with booking details once your booking has been confirmed. If you have any enquiries or need to change your booking, please feel free to contact us at <a href=\"tel:96504158\"><span class=\"s4\">96504158</span></a> or <a href=\"mailto:info@camerarental.biz\"><span class=\"s4\">info@camerarental.biz</span></a>. Kindly note that your booking is <b>NOT YET CONFIRMED</b>. </span><br><br>" +
            "<span class=\"s1\"><b>Name: </b> </span><span class=\"s2\">-user_name-</span><br>" +
            "<span class=\"s3\"><b>Contact number: </b></span><a href=\"tel:-contact_number-\"><span class=\"s5\">-contact_number-</span></a><br>" +
            "<span class=\"s3\"><b>NRIC/FIN/Passport No.: </b> </span><span class=\"s5\">-user_nric-</span><br><br>" +
            "<span class=\"s3\"><b>Rental date(s):</b></span><br>" +
            "-date- " +
            "<span class=\"s3\"><b>Equipment(s):</b></span><br>" +
            "-equipment_list- " +
            "<span class=\"s3\"><br><b>Comments:</b></span><br>" +
            "<span class=\"s2\"><i>-comment-</i></span><br><br><br>" +
            "<span class=\"s3\">Thank you!</span><br><br>" +
            "<span class=\"s3\">Regards,</span><br>" +
            "<span class=\"s3\">Camera Rental Centre</span>" +
            "</body>" +
            "</html>";

    public static final String INFO_HTML_BODY =
            "<html>" +
            "<head>" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
            "<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">" +
            "<style type=\"text/css\">" +
            "p.p1 {margin: 0.0px 0.0px 0.0px 0.0px; font: 14.0px Helvetica} " +
            "span.s1 {font-variant-ligatures: no-common-ligatures; color: #7f7f7f;} " +
            "span.s2 {text-decoration: underline ; font-kerning: none; color: #7f7f7f; -webkit-text-stroke: 0px #0068cf} " +
            "span.s3 {font-kerning: none; color: #7f7f7f; -webkit-text-stroke: 0px #000000} " +
            "</style>" +
            "</head>" +
            "<body>" +
            "<p class=\"p1\"><span class=\"s1\">If you do not receive an email reply to your booking enquiry within 24 hours, please call/SMS us at <a href=\"tel:96504158\"> <span class=\"s2\">96504158</span></a> or email to \"<a href=\"mailto:info@camerarental.biz\"> <span class=\"s2\">info@camerarental.biz</span></a>\" so that we can check on the status of your request.</span></p>" +
            "</body>" +
            "</html>";

    public static final String BOOKING_HTML =
            "<html>" +
            "<head>" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
            "<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">" +
            "<style type=\"text/css\">" +
            "p.p1 {margin: 0.0px 0.0px 0.0px 0.0px; font: 16.0px Helvetica} " +
            "p.p2 {margin: 0.0px 0.0px 0.0px 0.0px; font: 16.0px Helvetica; min-height: 19.0px} " +
            "span.s1 {font-variant-ligatures: no-common-ligatures} " +
            "span.s2 {text-decoration: underline ; font-kerning: none; color: #4787ff; -webkit-text-stroke: 0px #0068cf} " +
            "span.s3 {font-kerning: none; -webkit-text-stroke: 0px #000000} " +
            "</style>" +
            "</head>" +
            "<body>" +
            "<p class=\"p1\"><span class=\"s1\">Thank you. Please await our email for booking confirmation.</span></p>" +
            "<p class=\"p2\"><span class=\"s1\"></span><br></p>" +
            "<p class=\"p1\"><span class=\"s1\">If you do not hear back from us within 24 hours, please call/SMS us at <a href=\"tel:+6596504158\"> <span class=\"s2\">+65 96504158</span></a></span><span class=\"s3\"> </span><span class=\"s1\"> or email to </span><span class=\"s3\"> <a href=\"mailto:info@camerarental.biz\"> <span class=\"s2\">info@camerarental.biz</span></a></span><span class=\"s1\"> so that we can check on the status of your request.</span></p>" +
            "</body>" +
            "</html>";

}
