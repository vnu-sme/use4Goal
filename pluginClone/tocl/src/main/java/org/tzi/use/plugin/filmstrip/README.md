# Embedded Filmstrip core

These classes originate from the `FilmstripTransformation/trunk` sources in
the official `useocl/use_plugins` repository (commit
`3cc4e612235ab18812aa2fb3fe1bb11691f8f3bb`, author credited there as Frank
Hilken).

They are compiled as part of the TOCL plugin so that Filmstrip and TOCL use the
same USE Core API. The local adaptations cover the current classifier API,
the six-argument query-operation API, data-type visitor methods, and instance
constructor expressions. The standalone Filmstrip plugin must not be loaded
alongside TOCL.
