package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.ACCOUNT_BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.ACCOUNT_TABLE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.NAME_OF_BANK;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelperClass.NAME_OF_HOLDER;

public class PersistentStorageAccountDAO implements AccountDAO {
    private final DatabaseHelperClass databaseHelper;
    private SQLiteDatabase sqLiteDatabase;


    public PersistentStorageAccountDAO(Context context) {
        databaseHelper = new DatabaseHelperClass(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        sqLiteDatabase = databaseHelper.getReadableDatabase();

        String[] projection = {
                ACCOUNT_NO
        };

        Cursor cursor = sqLiteDatabase.query(
                ACCOUNT_TABLE,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        List<String> accountNumbersList = new ArrayList<String>();

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(ACCOUNT_NO));
            accountNumbersList.add(accountNumber);
        }
        cursor.close();
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        sqLiteDatabase = databaseHelper.getReadableDatabase();

        String[] projection = {
                ACCOUNT_NO,
                NAME_OF_BANK,
                NAME_OF_HOLDER,
                ACCOUNT_BALANCE
        };

        Cursor cursor = sqLiteDatabase.query(
                ACCOUNT_TABLE,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String bankName = cursor.getString(cursor.getColumnIndex(NAME_OF_BANK));
            String accountHolderName = cursor.getString(cursor.getColumnIndex(NAME_OF_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_BALANCE));
            Account account = new Account(accountNumber,bankName,accountHolderName,balance);

            accounts.add(account);
        }
        cursor.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        sqLiteDatabase = databaseHelper.getReadableDatabase();
        String[] projection = {
                ACCOUNT_NO,
                NAME_OF_BANK,
                NAME_OF_HOLDER,
                ACCOUNT_BALANCE
        };

        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = sqLiteDatabase.query(
                ACCOUNT_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor == null){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            cursor.moveToFirst();

            return new Account(accountNo, cursor.getString(cursor.getColumnIndex(NAME_OF_BANK)),
                    cursor.getString(cursor.getColumnIndex(NAME_OF_HOLDER)), cursor.getDouble(cursor.getColumnIndex(ACCOUNT_BALANCE)));
        }
    }

    @Override
    public void addAccount(Account account) {

        sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_NO, account.getAccountNo());
        contentValues.put(NAME_OF_BANK, account.getBankName());
        contentValues.put(NAME_OF_HOLDER, account.getAccountHolderName());
        contentValues.put(ACCOUNT_BALANCE,account.getBalance());

        sqLiteDatabase.insert(ACCOUNT_TABLE, null, contentValues);
        sqLiteDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(ACCOUNT_TABLE, ACCOUNT_NO + " = ?",
                new String[] { accountNo });
        sqLiteDatabase.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        String[] projection = {
                ACCOUNT_BALANCE
        };

        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = sqLiteDatabase.query(
                ACCOUNT_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        double balance;
        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues contentValues = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                contentValues.put(ACCOUNT_BALANCE, balance - amount);
                break;
            case INCOME:
                contentValues.put(ACCOUNT_BALANCE, balance + amount);
                break;
        }

        sqLiteDatabase.update(ACCOUNT_TABLE, contentValues, ACCOUNT_NO + " = ?",
                new String[] { accountNo });

        cursor.close();
        sqLiteDatabase.close();

    }
}