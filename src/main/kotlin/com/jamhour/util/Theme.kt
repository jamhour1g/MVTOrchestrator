package com.jamhour.util

import atlantafx.base.theme.CupertinoDark
import atlantafx.base.theme.CupertinoLight
import atlantafx.base.theme.Dracula
import atlantafx.base.theme.NordDark
import atlantafx.base.theme.NordLight
import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight

enum class Theme(val theme: atlantafx.base.theme.Theme) {
    PRIMER_LIGHT(PrimerLight()),
    PRIMER_DARK(PrimerDark()),
    CUPERTINO_LIGHT(CupertinoLight()),
    CUPERTINO_DARK(CupertinoDark()),
    NORD_LIGHT(NordLight()),
    NORD_DARK(NordDark()),
    DRACULA(Dracula());
}