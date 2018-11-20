package br.edu.utfpr.rogerio.inspalarm.DB;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import br.edu.utfpr.rogerio.inspalarm.model.Quote;
import br.edu.utfpr.rogerio.inspalarm.model.Schedule;
import br.edu.utfpr.rogerio.inspalarm.model.Tag;

@Database(entities = {Schedule.class, Tag.class, Quote.class}, version = 2)
public abstract class SchedulesDB extends RoomDatabase {

    private static final String DATABASE_NAME = "schedules.db";

    public abstract ScheduleDAO scheduleDAO();

    public abstract TagDAO tagDAO();

    public abstract  QuoteDAO quoteDAO();

    private static SchedulesDB instance;

    public static SchedulesDB getDatabase(final Context context) {


        if (instance == null) {

            synchronized (SchedulesDB.class) {
                if (instance == null) {
                    RoomDatabase.Builder builder =  Room.databaseBuilder(context,
                            SchedulesDB.class,
                            DATABASE_NAME);

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    InicialTags(context);
                                    //InicialQuotes(context);
                                }
                            });
                        }
                    });

                    instance = (SchedulesDB) builder.build();
                }
            }
        }

        return instance;
    }

    private static void InicialTags(final Context context){

        String[] tags = {"Motivational", "Inspirational", "Compliment"};

        for (String tag : tags) {

            Tag tagAux = new Tag(tag);

            instance.tagDAO().insert(tagAux);
        }
    }

    private static void InicialQuotes(final Context context){

        String[] quotes = {"Go do it !", "You can do it !", "You are awesome !"};

        int i = 0;

        for (String quote : quotes) {

            i++;
            Quote quoteAux = new Quote(quote, i);

            instance.quoteDAO().insert(quoteAux);
        }
    }
}
