package c.m.aurainteriorprojectadmin.ui.report

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.database.database
import c.m.aurainteriorprojectadmin.model.OrderExport
import c.m.aurainteriorprojectadmin.model.OrderSqlite
import c.m.aurainteriorprojectadmin.util.StringWithTag
import com.github.babedev.dexter.dsl.runtimePermission
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.activity_report.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.select
import org.jetbrains.anko.okButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class ReportActivity : AppCompatActivity() {

    private lateinit var selectQuery: SelectQueryBuilder
    private lateinit var monthList: Map<String, String>
    private lateinit var yearList: ArrayList<String>
    private lateinit var monthListAdapter: ArrayList<StringWithTag>
    private lateinit var monthSpinnerAdapter: ArrayAdapter<StringWithTag>
    private lateinit var yearSpinnerAdapter: ArrayAdapter<String>
    private lateinit var stringWithTag: StringWithTag
    private lateinit var progressDialog: ProgressDialog
    private lateinit var fileOutputStream: FileOutputStream
    private lateinit var directoryLocation: File
    private lateinit var fileName: File
    private lateinit var document: Document
    private lateinit var titleFont: Font
    private lateinit var subTitleFont: Font
    private lateinit var bodyFontNormal: Font
    private lateinit var bodyFontUnderline: Font
    private var filePath: String? = ""
    private var monthKey: String? = ""
    private var monthValue: String? = ""
    private var keySpinnerMonth: String? = ""
    private var valueSpinnerMonth: String? = ""
    private var selectedYear: String? = ""
    private var contentDataSqlite: MutableList<OrderExport> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        supportActionBar?.apply {
            title = getString(R.string.export_report)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // permission device
        permissionDevice()

        monthList = mapOf(
            "All" to "Ekspor Semua Laporan dalam 1 Tahun",
            "01" to "Januari",
            "02" to "Februari",
            "03" to "Maret",
            "04" to "April",
            "05" to "Mei",
            "06" to "Juni",
            "07" to "Juli",
            "08" to "Agustus",
            "09" to "September",
            "10" to "Oktober",
            "11" to "November",
            "12" to "Desember"
        )
        yearList = arrayListOf()
        monthListAdapter = ArrayList()

        for (i in 2000..2100) {
            yearList.add(i.toString())
        }

        monthList.forEach {
            monthKey = it.key
            monthValue = it.value
            monthListAdapter.add(StringWithTag(monthKey.toString(), monthValue.toString()))
        }

        monthSpinnerAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, monthListAdapter)

        spinner_choose_month.apply {
            adapter = monthSpinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    stringWithTag = parent?.getItemAtPosition(position) as StringWithTag
                    keySpinnerMonth = stringWithTag.tag.toString()
                    valueSpinnerMonth = stringWithTag.string
                }
            }
        }

        yearSpinnerAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, yearList)

        spinner_choose_year.apply {
            adapter = yearSpinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedYear = spinner_choose_year.selectedItem.toString()
                }
            }
        }

        btn_export_pdf.setOnClickListener {
            // get data from sqlite
            getDataFromSqlite(keySpinnerMonth.toString(), selectedYear.toString())

            // export pdf
            exportPDF(
                keySpinnerMonth.toString(),
                valueSpinnerMonth.toString(),
                selectedYear.toString()
            )
        }
    }

    private fun getDataFromSqlite(month: String, year: String) {
        val collectionDataSql = ArrayList<OrderExport>()

        this.database.use {
            selectQuery = when (month == "All") {
                true -> {
                    select(OrderSqlite.TABLE_ORDER)
                        .whereArgs("ORDER_DATE LIKE '%$year%'")
                        .orderBy("_ID", SqlOrderDirection.ASC)
                }
                false -> {
                    select(OrderSqlite.TABLE_ORDER)
                        .whereArgs("ORDER_DATE Like '%$month%$year'")
                        .orderBy("_ID", SqlOrderDirection.ASC)
                }
            }

            selectQuery.parseList(object : MapRowParser<List<OrderExport>> {
                override fun parseRow(columns: Map<String, Any?>): List<OrderExport> {
                    val orderExport = OrderExport(
                        columns.getValue("_ID") as Long?,
                        columns.getValue("NAME") as String?,
                        columns.getValue("ADDRESS") as String?,
                        columns.getValue("PHONE") as String?,
                        columns.getValue("TYPE_WALLPAPER") as String?,
                        columns.getValue("ORDER_DATE") as String?
                    )

                    collectionDataSql.add(orderExport)
                    return collectionDataSql
                }
            })
        }

        // get data
        contentDataSqlite.clear()
        contentDataSqlite.addAll(collectionDataSql)
    }

    @SuppressLint("SimpleDateFormat")
    private fun exportPDF(numberMonth: String, nameMonth: String, year: String) {
        // show progress dialog
        showProgressDialog("Ekspor Laporan $nameMonth - $year")

        // create file and directory PDF
        document = Document()
        try {
            filePath =
                Environment.getExternalStorageDirectory().absolutePath + "/Download/ReportAuraAdminPDF"
            directoryLocation = File(filePath as String)
            if (!directoryLocation.exists()) directoryLocation.mkdir()
            fileName = File(directoryLocation, "Laporan Penjualan $nameMonth - Tahun $year.pdf")
            if (!fileName.exists()) fileName.delete()
            fileOutputStream = FileOutputStream(fileName)
        } catch (e: IOException) {
            permissionDevice()
        }

        // generate PDF
        PdfWriter.getInstance(document, fileOutputStream)
        with(document) {
            open()
            pageSize = PageSize.A4 // page size
            titleFont = Font(
                Font.FontFamily.TIMES_ROMAN,
                14.0f,
                Font.BOLD,
                BaseColor.BLACK
            )
            subTitleFont = Font(
                Font.FontFamily.TIMES_ROMAN,
                12.0f,
                Font.BOLD,
                BaseColor.BLACK
            )
            bodyFontNormal = Font(
                Font.FontFamily.TIMES_ROMAN,
                12.0f,
                Font.NORMAL,
                BaseColor.BLACK
            )
            bodyFontUnderline = Font(
                Font.FontFamily.TIMES_ROMAN,
                12.0f,
                Font.UNDERLINE,
                BaseColor.BLACK
            )
            addCreationDate() // document date create
            // add title
            add(
                Paragraph(
                    Chunk(
                        "Laporan Penjualan Aura Interior Project",
                        titleFont
                    )
                ).apply {
                    alignment = Element.ALIGN_CENTER
                }
            )
            // add sub title
            add(
                Paragraph(
                    when (numberMonth == "All") {
                        true -> {
                            Chunk(
                                "Laporan Tahun $year",
                                subTitleFont
                            )
                        }
                        false -> {
                            Chunk(
                                "Laporan Bulan $nameMonth Tahun $year",
                                subTitleFont
                            )
                        }
                    }
                ).apply {
                    alignment = Element.ALIGN_CENTER
                }
            )
            add(Paragraph("\n\n")) // next line
            // table report information
            add(
                PdfPTable(floatArrayOf(1f, 5f)).apply {
                    widthPercentage = 100f
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "Dokumen : ",
                                bodyFontNormal
                            )
                        ).apply {
                            border = Rectangle.NO_BORDER
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "Laporan Penjualan Wallpaper Aura Interior Project",
                                bodyFontNormal
                            )
                        ).apply {
                            border = Rectangle.NO_BORDER
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "Bulan : ",
                                bodyFontNormal
                            )
                        ).apply {
                            border = Rectangle.NO_BORDER
                        }
                    )
                    addCell(
                        when (numberMonth == "All") {
                            true -> {
                                PdfPCell(
                                    Paragraph(
                                        "-",
                                        bodyFontNormal
                                    )
                                ).apply {
                                    border = Rectangle.NO_BORDER
                                }
                            }
                            false -> {
                                PdfPCell(
                                    Paragraph(
                                        nameMonth,
                                        bodyFontNormal
                                    )
                                ).apply {
                                    border = Rectangle.NO_BORDER
                                }
                            }
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "Tahun : ",
                                bodyFontNormal
                            )
                        ).apply {
                            border = Rectangle.NO_BORDER
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                year,
                                bodyFontNormal
                            )
                        ).apply {
                            border = Rectangle.NO_BORDER
                        }
                    )
                }
            )
            add(Paragraph("\n\n")) // next line
            // table report data
            add(
                PdfPTable(floatArrayOf(2f, 6f, 6f, 6f, 6f, 6f)).apply {
                    widthPercentage = 100f
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "NO.",
                                bodyFontNormal
                            )
                        ).apply {
                            horizontalAlignment = Element.ALIGN_CENTER
                            verticalAlignment = Element.ALIGN_MIDDLE
                            rowspan = 2
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "NAMA.",
                                bodyFontNormal
                            )
                        ).apply {
                            horizontalAlignment = Element.ALIGN_CENTER
                            verticalAlignment = Element.ALIGN_MIDDLE
                            rowspan = 2
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "ALAMAT.",
                                bodyFontNormal
                            )
                        ).apply {
                            horizontalAlignment = Element.ALIGN_CENTER
                            verticalAlignment = Element.ALIGN_MIDDLE
                            rowspan = 2
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "TELEPON.",
                                bodyFontNormal
                            )
                        ).apply {
                            horizontalAlignment = Element.ALIGN_CENTER
                            verticalAlignment = Element.ALIGN_MIDDLE
                            rowspan = 2
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "TIPE WALLPAPER",
                                bodyFontNormal
                            )
                        ).apply {
                            horizontalAlignment = Element.ALIGN_CENTER
                            verticalAlignment = Element.ALIGN_MIDDLE
                            rowspan = 2
                        }
                    )
                    addCell(
                        PdfPCell(
                            Paragraph(
                                "TANGGAL PESAN",
                                bodyFontNormal
                            )
                        ).apply {
                            horizontalAlignment = Element.ALIGN_CENTER
                            verticalAlignment = Element.ALIGN_MIDDLE
                            rowspan = 2
                        }
                    )
                    if (contentDataSqlite.isNotEmpty()) {
                        contentDataSqlite.forEachIndexed { index, orderExport ->
                            addCell(
                                PdfPCell(
                                    Paragraph(
                                        "${index + 1}",
                                        bodyFontNormal
                                    )
                                ).apply {
                                    horizontalAlignment = Element.ALIGN_CENTER
                                    verticalAlignment = Element.ALIGN_MIDDLE
                                }
                            )
                            addCell(
                                PdfPCell(
                                    Paragraph(
                                        orderExport.name,
                                        bodyFontNormal
                                    )
                                ).apply {
                                    horizontalAlignment = Element.ALIGN_CENTER
                                    verticalAlignment = Element.ALIGN_MIDDLE
                                }
                            )
                            addCell(
                                PdfPCell(
                                    Paragraph(
                                        orderExport.address,
                                        bodyFontNormal
                                    )
                                ).apply {
                                    horizontalAlignment = Element.ALIGN_CENTER
                                    verticalAlignment = Element.ALIGN_MIDDLE
                                }
                            )
                            addCell(
                                PdfPCell(
                                    Paragraph(
                                        orderExport.phone,
                                        bodyFontNormal
                                    )
                                ).apply {
                                    horizontalAlignment = Element.ALIGN_CENTER
                                    verticalAlignment = Element.ALIGN_MIDDLE
                                }
                            )
                            addCell(
                                PdfPCell(
                                    Paragraph(
                                        orderExport.typeWallpaperOrder,
                                        bodyFontNormal
                                    )
                                ).apply {
                                    horizontalAlignment = Element.ALIGN_CENTER
                                    verticalAlignment = Element.ALIGN_MIDDLE
                                }
                            )
                            addCell(
                                PdfPCell(
                                    Paragraph(
                                        orderExport.orderDate,
                                        bodyFontNormal
                                    )
                                ).apply {
                                    horizontalAlignment = Element.ALIGN_CENTER
                                    verticalAlignment = Element.ALIGN_MIDDLE
                                }
                            )
                        }
                    }
                }
            )
            add(Paragraph("\n\n")) // next line
            close()
        }

        // close progress dialog
        closeProgressDialog()

        // alert success export
        alert(
            "Laporan anda tersimpan di folder Download -> ReportAuraAdminPDF",
            "Ekspor Laporan"
        ) {
            okButton {}
        }.apply {
            isCancelable = false
            show()
        }
    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.apply {
            title = message
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun closeProgressDialog() {
        progressDialog.dismiss()
    }

    private fun permissionDevice() {
        runtimePermission {
            permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                checked { }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
