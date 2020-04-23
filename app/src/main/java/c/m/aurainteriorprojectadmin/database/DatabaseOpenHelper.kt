package c.m.aurainteriorprojectadmin.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import c.m.aurainteriorprojectadmin.model.OrderSqlite
import org.jetbrains.anko.db.*

class DatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "aura.db", null, 1) {
    companion object {
        private var instance: DatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseOpenHelper {
            if (instance == null) {
                instance = DatabaseOpenHelper(ctx.applicationContext)
            }

            return instance as DatabaseOpenHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(
            OrderSqlite.TABLE_ORDER, true,
            OrderSqlite.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            OrderSqlite.UID to TEXT,
            OrderSqlite.NAME to TEXT,
            OrderSqlite.ADDRESS to TEXT,
            OrderSqlite.PHONE to TEXT,
            OrderSqlite.LATITUDE to TEXT,
            OrderSqlite.LONGITUDE to TEXT,
            OrderSqlite.TYPE_WALLPAPER to TEXT,
            OrderSqlite.PRICE_ESTIMATION to TEXT,
            OrderSqlite.ROLL_ESTIMATION to TEXT,
            OrderSqlite.CUSTOMER_UID to TEXT,
            OrderSqlite.ORDER_STATUS to TEXT,
            OrderSqlite.ORDER_DATE to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(OrderSqlite.TABLE_ORDER, true)
    }
}

val Context.database: DatabaseOpenHelper
    get() = DatabaseOpenHelper.getInstance(applicationContext)
