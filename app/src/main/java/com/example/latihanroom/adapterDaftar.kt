package com.example.latihanroom

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.latihanroom.database.daftarBelanja
import com.example.latihanroom.database.daftarBelanjaDB
import com.example.latihanroom.database.historyBarang
import com.example.latihanroom.database.historyBarangDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class adapterDaftar (private val daftarBelanja : MutableList<daftarBelanja>): RecyclerView.Adapter<adapterDaftar.ListViewHolder>() {
    class ListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvItemBarang = itemView.findViewById<TextView>(R.id.tvItemBarang)
        var _tvjumlahBarang = itemView.findViewById<TextView>(R.id.tvjumlahBarang)
        var _tvTanggal = itemView.findViewById<TextView>(R.id.tvTanggal)

        var _btnSelesai = itemView.findViewById<Button>(R.id.btnSelesai)
        var _btnEdit = itemView.findViewById<ImageView>(R.id.btnEdit)
        var _btnDelete = itemView.findViewById<ImageView>(R.id.btnDelete)
    }

    private lateinit var db1: daftarBelanjaDB
    private lateinit var db2: historyBarangDB

    fun setDatabaseReferences(db1: daftarBelanjaDB, db2: historyBarangDB) {
        this.db1 = db1
        this.db2 = db2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): adapterDaftar.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_list, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarBelanja.size
    }

    override fun onBindViewHolder(holder: adapterDaftar.ListViewHolder, position: Int) {
        var daftar = daftarBelanja[position]

        holder._tvTanggal.setText(daftar.tanggal)
        holder._tvItemBarang.setText(daftar.item)
        holder._tvjumlahBarang.setText(daftar.jumlah)

        holder._btnEdit.setOnClickListener {
            val intent = Intent(it.context, TambahDaftar::class.java)
            intent.putExtra("id", daftar.id)
            intent.putExtra("addEdit", 1)
            it.context.startActivity(intent)
        }

        holder._btnDelete.setOnClickListener {
            onItemClickCallback.delData(daftar)
        }

        holder._btnSelesai.setOnClickListener {
            CoroutineScope(Dispatchers.IO).async {
                val historyDAO = db2.funhistoryBarangDAO()
                historyDAO.insert(
                    historyBarang(
                        item = daftar.item,
                        jumlah = daftar.jumlah,
                        tanggal = daftar.tanggal
                    )
                )

                val daftarDAO = db1.fundaftarBelanjaDAO()
                daftarDAO.delete(daftar)
            }
        }

    }



    interface OnItemClickCallback {
        fun delData(dtBelanja: daftarBelanja)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun isiData(daftar: List<daftarBelanja>) {
        daftarBelanja.clear()
        daftarBelanja.addAll(daftar)
        notifyDataSetChanged()
    }



    private lateinit var onItemClickCallback : OnItemClickCallback


}