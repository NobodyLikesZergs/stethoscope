package edu.phystech.stethoscope.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import edu.phystech.stethoscope.domain.Audio;

public class Utils {

    private static final String DATE_PATTERN = "dd:MM:yyyy";

    public static String dateToString(DateTime dateTime) {
        return dateTime.toString(DATE_PATTERN);
    }

    public static DateTime stringToDate(String string) {
        return DateTimeFormat.forPattern(DATE_PATTERN).parseDateTime(string);
    }

    public static Audio getLastAudioWithPoint(List<Audio> audioList, int point) {
        int maxNumber = 0;
        Audio res = null;
        for (Audio audio: audioList) {
            if (audio.getPoint() == point && audio.getNumber() > maxNumber) {
                maxNumber = audio.getNumber();
                res = audio;
            }
        }
        return res;
    }

    public static int getLastAudioNumWithPoint(List<Audio> audioList, int point) {
        Audio audio = getLastAudioWithPoint(audioList, point);
        if (audio == null) {
            return 0;
        } else {
            return audio.getNumber();
        }
    }

    public static boolean hasAudiosWithPoint(List<Audio> audios, int point) {
        if (getLastAudioWithPoint(audios, point) != null) {
            return true;
        } else {
            return false;
        }
    }

}
