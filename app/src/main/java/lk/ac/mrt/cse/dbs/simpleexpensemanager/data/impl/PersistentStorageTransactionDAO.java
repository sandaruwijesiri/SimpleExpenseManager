package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.EXPENSE_TYPE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.TRANSACTION_TABLE;

public class PersistentStorageTransactionDAO implements TransactionDAO {
    private final DatabaseHelperClass databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public PersistentStorageTransactionDAO(Context context) {
        databaseHelper = new DatabaseHelperClass(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, dateFormat.format(date));
        contentValues.put(ACCOUNT_NO, accountNo);
        contentValues.put(EXPENSE_TYPE, String.valueOf(expenseType));
        contentValues.put(AMOUNT, amount);

        sqLiteDatabase.insert(TRANSACTION_TABLE, null, contentValues);
        sqLiteDatabase.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new ArrayList<Transaction>();

        sqLiteDatabase = databaseHelper.getReadableDatabase();

        String[] projection = {
                DATE,
                ACCOUNT_NO,
                EXPENSE_TYPE,
                AMOUNT
        };

        Cursor cursor = sqLiteDatabase.query(
                TRANSACTION_TABLE,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String type = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(date1,accountNumber,expenseType,amount);

            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {

        List<Transaction> transactions = new ArrayList<Transaction>();

        sqLiteDatabase = databaseHelper.getReadableDatabase();

        String[] projection = {
                DATE,
                ACCOUNT_NO,
                EXPENSE_TYPE,
                AMOUNT
        };

        Cursor cursor = sqLiteDatabase.query(
                TRANSACTION_TABLE,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        int size = cursor.getCount();

        while(cursor.moveToNext()) {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString(cursor.getColumnIndex(DATE)));
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String type = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(date,accountNumber,expenseType,amount);

            transactions.add(transaction);
        }

        if (size <= limit) {
            return transactions;
        }

        return transactions.subList(size - limit, size);
    }
}