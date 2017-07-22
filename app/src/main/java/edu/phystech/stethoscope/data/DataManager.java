package edu.phystech.stethoscope.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import edu.phystech.stethoscope.domain.Audio;
import edu.phystech.stethoscope.domain.Person;
import edu.phystech.stethoscope.utils.Utils;

public class DataManager {

    private DataBaseHelper dbHelper;

    @Inject
    public DataManager (DataBaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Person savePerson(String firstName, String lastName,
                             int age, String date, String comment) {
        ContentValues cv = new ContentValues();
        cv.put(DataBaseHelper.FIRST_NAME, firstName);
        cv.put(DataBaseHelper.LAST_NAME, lastName);
        cv.put(DataBaseHelper.AGE, age);
        cv.put(DataBaseHelper.DATE, date);
        cv.put(DataBaseHelper.COMMENT, comment);
        long id = dbHelper.getWritableDatabase().insert(DataBaseHelper.PERSON_TABLE, null, cv);
        return new Person(id, firstName,
                lastName, age, Utils.stringToDate(date), comment);
    }

    public void removePerson(Person person) {
        removeAudiosByPerson(person);
        String whereClause = DataBaseHelper.ID + "=?";
        String[] args = {""+person.getId(),};
        dbHelper.getWritableDatabase().delete(DataBaseHelper.PERSON_TABLE, whereClause, args);
    }

    private List<Person> getPersonList(String selection, String[] selectionArgs) {
        String[] columns = {
                DataBaseHelper.ID,
                DataBaseHelper.FIRST_NAME,
                DataBaseHelper.LAST_NAME,
                DataBaseHelper.AGE,
                DataBaseHelper.DATE,
                DataBaseHelper.COMMENT,
        };
        Cursor c = dbHelper.getReadableDatabase().query(DataBaseHelper.PERSON_TABLE,
                columns, selection, selectionArgs, null, null, null);
        List<Person> result = new ArrayList<>();
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(DataBaseHelper.ID));
            String firstName = c.getString(c.getColumnIndex(DataBaseHelper.FIRST_NAME));
            String lastName = c.getString(c.getColumnIndex(DataBaseHelper.LAST_NAME));
            int age = c.getInt(c.getColumnIndex(DataBaseHelper.AGE));
            String stringDate = c.getString(c.getColumnIndex(DataBaseHelper.DATE));
            String comment = c.getString(c.getColumnIndex(DataBaseHelper.COMMENT));
            result.add(new Person(id, firstName, lastName,
                    age, Utils.stringToDate(stringDate), comment));
        }
        return result;
    }

    public List<Person> getAllPersons() {
        return getPersonList(null, null);
    }

    public Person getPersonById(long id) {
        String selection = DataBaseHelper.ID + "=?";
        String[] args = {""+id,};
        List<Person> resultList = getPersonList(selection, args);
        return resultList.get(0);
    }

    public Audio saveAudio(long personId, boolean isHeart, int point,
                           int number, String filePath) {
        ContentValues cv = new ContentValues();
        cv.put(DataBaseHelper.PERSON_ID, personId);
        cv.put(DataBaseHelper.IS_HEART, isHeart);
        cv.put(DataBaseHelper.POINT, point);
        cv.put(DataBaseHelper.NUMBER, number);
        cv.put(DataBaseHelper.FILE_PATH, filePath);
        long id = dbHelper.getWritableDatabase().insert(DataBaseHelper.AUDIO_TABLE, null, cv);
        return new Audio(id, personId, isHeart, point, number, filePath);
    }

    public void removeAudio(Audio audio) {
        String whereClause = DataBaseHelper.ID + "=?";
        String[] args = {""+audio.getId(),};
        dbHelper.getWritableDatabase().delete(DataBaseHelper.AUDIO_TABLE, whereClause, args);
    }

    public void removeAudiosByPerson(Person person) {
        List<Audio> audioList = getAudiosByPerson(person);
        for (Audio a: audioList) {
            removeAudio(a);
        }
    }

    private List<Audio> getAudioList(String selection, String[] selectionArgs) {
        String[] columns = {
                DataBaseHelper.ID,
                DataBaseHelper.POINT,
                DataBaseHelper.IS_HEART,
                DataBaseHelper.NUMBER,
                DataBaseHelper.FILE_PATH,
                DataBaseHelper.PERSON_ID,
        };
        Cursor c = dbHelper.getReadableDatabase().query(DataBaseHelper.AUDIO_TABLE,
                columns, selection, selectionArgs, null, null, null);
        List<Audio> result = new ArrayList<>();
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(DataBaseHelper.ID));
            int point = c.getInt(c.getColumnIndex(DataBaseHelper.POINT));
            boolean isHeart = c.getInt(c.getColumnIndex(DataBaseHelper.IS_HEART)) != 0;
            int number = c.getInt(c.getColumnIndex(DataBaseHelper.NUMBER));
            String filePath = c.getString(c.getColumnIndex(DataBaseHelper.FILE_PATH));
            long personId = c.getLong(c.getColumnIndex(DataBaseHelper.PERSON_ID));
            result.add(new Audio(id, personId, isHeart, point, number, filePath));
        }
        return result;
    }

    public List<Audio> getAudiosByPerson(Person person) {
        String selection = DataBaseHelper.PERSON_ID + " = ?";
        String[] args = {""+person.getId(), };
        return getAudioList(selection, args);
    }
}
