package com.limetac.scanner.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.repository.*
import com.limetac.scanner.ui.view.pkg.PackageViewModel
import com.limetac.scanner.ui.view.antenna.AntennaViewModel
import com.limetac.scanner.ui.view.bin.BinViewModel
import com.limetac.scanner.ui.view.main.MainViewModel
import com.limetac.scanner.ui.view.scanHelper.ScanHelperViewModel
import com.limetac.scanner.ui.view.tag.TagEntityViewModel
import com.limetac.scanner.ui.view.tagEntity.TagScanningViewModel

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(PackageViewModel::class.java)) {
            return PackageViewModel(PkgRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(BinViewModel::class.java)) {
            return BinViewModel(BinRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(AntennaViewModel::class.java)) {
            return AntennaViewModel(AntennaRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(TagEntityViewModel::class.java)) {
            return TagEntityViewModel(EntityRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(TagScanningViewModel::class.java)) {
            return TagScanningViewModel(EntityRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(ScanHelperViewModel::class.java)) {
            return ScanHelperViewModel(ScanHelperRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}