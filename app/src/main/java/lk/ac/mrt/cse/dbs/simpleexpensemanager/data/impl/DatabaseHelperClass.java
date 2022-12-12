package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperClass extends SQLiteOpenHelper {

    private static final String NAME_OF_DATABASE = "200729V";
    private static final int VERSION = 1;

    public static final String ACCOUNT_TABLE = "account";
    public static final String TRANSACTION_TABLE = "transac";

    public static final String ACCOUNT_NO = "accountNo";

    public static final String NAME_OF_BANK = "bankName";
    public static final String NAME_OF_HOLDER = "accountHolderName";
    public static final String ACCOUNT_BALANCE = "balance";

    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";


    public DatabaseHelperClass(Context context) {
        super(context, NAME_OF_DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + ACCOUNT_TABLE + "(" +
                ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                NAME_OF_BANK + " TEXT NOT NULL, " +
                NAME_OF_HOLDER + " TEXT NOT NULL, " +
                ACCOUNT_BALANCE + " REAL NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TRANSACTION_TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " TEXT NOT NULL, " +
                EXPENSE_TYPE + " TEXT NOT NULL, " +
                AMOUNT + " REAL NOT NULL, " +
                ACCOUNT_NO + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NO + ") REFERENCES " + ACCOUNT_TABLE + "(" + ACCOUNT_NO + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);


        onCreate(sqLiteDatabase);
    }

}