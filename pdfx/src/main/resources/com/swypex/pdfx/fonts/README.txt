Cairo font files

Cairo is by the Cairo Project Authors and is distributed under the SIL Open
Font License 1.1 in OFL.txt. These static regular and bold instances come from
https://github.com/expo/google-fonts/tree/master/font-packages/cairo (the Cairo
project is https://github.com/Gue3bara/Cairo).

The bundled files have an adjusted Unicode character map: isolated Arabic
presentation-form characters are aliases of their corresponding base glyphs.
Cairo uses the same glyphs for these forms, but OpenPDF emits presentation-form
code points during Arabic shaping. Without these aliases those letters vanish.
The outlines and other font data are unchanged.
