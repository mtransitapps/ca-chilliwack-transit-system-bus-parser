package org.mtransit.parser.ca_chilliwack_transit_system_bus;

import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.mt.data.MAgency;

import java.util.Locale;
import java.util.regex.Pattern;

// https://www.bctransit.com/open-data
public class ChilliwackTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new ChilliwackTransitSystemBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@Override
	public boolean excludeRoute(@NotNull GRoute gRoute) {
		if (gRoute.getRouteLongNameOrDefault().contains("FVX")) {
			return EXCLUDE; // available in Fraser Valley Express app
		}
		final int rsn = Integer.parseInt(gRoute.getRouteShortName());
		if (rsn < 50) {
			return EXCLUDE; // available in Central Fraser Valley app
		}
		return super.excludeRoute(gRoute);
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Chilliwack TS";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		return switch (gRoute.getRouteShortName()) {
			// @formatter:off
			case "1"->"E21735";
			case "2"->"7FB539";
			case "3"->"004A8F";
			case "4"->"F78B1F";
			case "5"->"52A6ED";
			case "6"->"7C3F24";
			case "7"->"A3238E";
			case "8"->"49176D";
			case "9"->"4F6F19";
			case "11"->"FCAF17";
			case "22"->"009C3B";
			case "51"->"E51535";
			case "52"->"80CC28";
			case "53"->"61849C";
			case "54"->"F68712";
			case "55"->"00ADEF";
			case "56"->"75321E";
			case "57"->"A32B9B";
			case "58"->"401A64";
			case "59"->"49690F";
			case "66"->AGENCY_COLOR_BLUE;
			case "71"->"FBBD09";
			case "72"->"11AB4A";
			// @formatter:on
			default -> throw new MTLog.Fatal("Unexpected missing route color for %s!", gRoute);
		};
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^\\d+\\S+)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, tripHeadsign, getIgnoredWords());
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private String[] getIgnoredWords() {
		return new String[]{"AM", "PM"};
	}

	private static final Pattern STARTS_WITH_DCOM = Pattern.compile("(^(\\(-DCOM-\\)))", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_IMPL = Pattern.compile("(^(\\(-IMPL-\\)))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = STARTS_WITH_DCOM.matcher(gStopName).replaceAll(EMPTY);
		gStopName = STARTS_WITH_IMPL.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
