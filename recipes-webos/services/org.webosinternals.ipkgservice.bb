SUMMARY = "Package management service for Open webOS"
SECTION = "webos/services"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb723b61539feef013de476e68b5c50a"

DEPENDS = "luna-service2 glib-2.0 json-c"

PV = "2.0.0-2+git${SRCPV}"
SRCREV = "16a946e0b07e02968570b01fdf91f1f62b3c9c09"

inherit webos_ports_repo
inherit webos_cmake
inherit pkgconfig
inherit webos_system_bus

WEBOS_REPO_NAME = "preware"
SRC_URI = "${WEBOS_PORTS_GIT_REPO_COMPLETE}"

S = "${WORKDIR}/git/oe-service"

pkg_postinst_${PN}() {
    #!/bin/sh -e

    # Make sure we're only executed on the device
    if [ x"$D" = "x" ]; then
        APPS=/media/cryptofs/apps

        # Create the opkg config and database areas
        mkdir -p $APPS/${sysconfdir}/opkg $APPS/${localstatedir}/lib/opkg/cache

        # We provide an empty status file to satisfy the ipkgservice
        touch $APPS/${localstatedir}/lib/opkg/status

        # Remove all list database cache files
        rm -f $APPS/${localstatedir}/lib/opkg/lists/*

        # Set up the architecture configuration file
        rm -f $APPS/${sysconfdir}/opkg/arch.conf
        cp ${sysconfdir}/opkg/arch.conf $APPS/${sysconfdir}/opkg/arch.conf

        # Install webosports all-arch feeds
        echo "src/gz webosports http://feeds.webos-ports.org/webos-ports/all" > $APPS/${sysconfdir}/opkg/webos-ports.conf

        # Add additional feeds which are disabled by default and NOT SUPPORTED by webOS
        # ports / LuneOS. The user has to turn them on manually to use them.
        echo "src Macaw-enyo http://minego.net/preware/macaw-enyo" > $APPS/${sysconfdir}/opkg/macaw-enyo.conf.disabled
        echo "src Hominid-Software http://hominidsoftware.com/preware" > $APPS/${sysconfdir}/opkg/hominid-software.conf.disabled
        echo "src/gz FeedSpider2 http://feedspider.net/luneos" > $APPS/${sysconfdir}/opkg/feedspider.conf.disabled
    else
        exit 1
    fi
}
