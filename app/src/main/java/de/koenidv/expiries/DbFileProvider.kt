package de.koenidv.expiries

import androidx.core.content.FileProvider

class DbFileProvider : FileProvider {
    constructor() : super(R.xml.file_paths)


}