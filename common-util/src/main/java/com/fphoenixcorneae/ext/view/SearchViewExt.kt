package com.fphoenixcorneae.ext.view

import androidx.appcompat.widget.SearchView

fun SearchView.queryTextListener(listener: KtxQueryTextLister.() -> Unit) {
    setOnQueryTextListener(KtxQueryTextLister().apply(listener))
}

class KtxQueryTextLister : SearchView.OnQueryTextListener {

    private var _onQueryTextSubmit: ((String?) -> Unit)? = null
    private var _onQueryTextChange: ((String?) -> Unit)? = null

    fun onQueryTextSubmit(listener: ((String?) -> Unit)?) {
        _onQueryTextSubmit = listener
    }

    fun onQueryTextChange(listener: (String?) -> Unit) {
        _onQueryTextChange = listener
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        _onQueryTextSubmit?.invoke(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        _onQueryTextChange?.invoke(newText)
        return false
    }

}