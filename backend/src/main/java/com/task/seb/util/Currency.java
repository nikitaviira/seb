package com.task.seb.util;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;

@Getter
public enum Currency {
  USD("US Dollar"),
  EUR("Euro"),
  GBP("Pound Sterling"),
  JPY("Japanese Yen"),
  CNY("Chinese Yuan Renminbi"),
  CAD("Canadian Dollar"),
  AUD("Australian Dollar"),
  INR("Indian Rupee"),
  NZD("New Zealand Dollar"),
  CHF("Swiss Franc"),
  ZAR("South African Rand"),
  RUB("Russian Ruble"),
  SEK("Swedish Krona"),
  MXN("Mexican Peso"),
  SGD("Singapore Dollar"),
  HKD("Hong Kong Dollar"),
  NOK("Norwegian Krone"),
  KRW("South Korean Won"),
  TRY("Turkish Lira"),
  BRL("Brazilian Real"),
  DKK("Danish Krone"),
  PLN("Polish Zloty"),
  TWD("Taiwan New Dollar"),
  THB("Thai Baht"),
  MYR("Malaysian Ringgit"),
  IDR("Indonesian Rupiah"),
  PHP("Philippine Peso"),
  SAR("Saudi Riyal"),
  AED("UAE Dirham"),
  QAR("Qatari Rial"),
  KWD("Kuwaiti Dinar"),
  BHD("Bahraini Dinar"),
  OMR("Rial Omani"),
  VND("Vietnamese Dong"),
  EGP("Egyptian Pound"),
  MUR("Mauritius Rupee"),
  UAH("Ukrainian Hryvnia"),
  HUF("Hungarian Forint"),
  CZK("Czech Koruna"),
  ILS("Israeli Shekel"),
  CLP("Chilean Peso"),
  PEN("Peruvian Sol"),
  COP("Colombian Peso"),
  ARS("Argentine Peso"),
  GHS("Ghana Cedi"),
  NGN("Nigerian Naira"),
  KES("Kenyan Shilling"),
  UGX("Uganda Shilling"),
  ZMW("Zambian Kwacha"),
  MZN("Mozambique Metical"),
  TND("Tunisian Dinar"),
  MAD("Moroccan Dirham"),
  DZD("Algerian Dinar"),
  LBP("Lebanese Pound"),
  RSD("Serbian Dinar"),
  LYD("Libyan Dinar"),
  JOD("Jordanian Dinar"),
  BWP("Botswana Pula"),
  GMD("Gambian Dalasi"),
  SZL("Swazi Lilangeni"),
  LSL("Basotho Loti"),
  MGA("Malagasy Ariary"),
  MWK("Malawi Kwacha"),
  SCR("Seychelles Rupee"),
  AFN("Afghan Afghani"),
  TJS("Tajikistan Somoni"),
  SOM("Somali Shilling"),
  BTN("Bhutanese Ngultrum"),
  ERN("Eritrean Nakfa"),
  MVR("Maldivian Rufiyaa"),
  NAD("Namibia Dollar"),
  SRD("Surinamese Dollar"),
  XOF("CFA Franc BCEAO"),
  ZWL("Zimbabwe Dollar"),
  FJD("Fiji Dollar"),
  BBD("Barbados Dollar"),
  GTQ("Guatemalan Quetzal"),
  HNL("Honduran Lempira"),
  PYG("Paraguayan Guarani"),
  BOB("Bolivian Boliviano"),
  UYU("Peso Uruguayo"),
  CRC("Costa Rican Colon"),
  BSD("Bahamian Dollar"),
  CUP("Cuban Peso"),
  SVC("Salvadoran Colon"),
  BZD("Belize Dollar"),
  ANG("Dutch Guilder"),
  SBD("Solomon Islands Dollar"),
  BND("Brunei Dollar"),
  KYD("Cayman Islands Dollar"),
  AWG("Aruban Florin"),
  FKP("Falkland Islands Pound"),
  GIP("Gibraltar Pound"),
  SHP("Saint Helena Pound"),
  JMD("Jamaican Dollar"),
  TTD("Trinidadian Dollar"),
  VUV("Ni-Vanuatu Vatu"),
  WST("Samoan Tala"),
  KMF("Comorian Franc"),
  TOP("Tongan Pa'anga"),
  MOP("Macanese Pataca"),
  PGK("Papua New Guinean Kina"),
  UZS("Uzbekistan Som"),
  MNT("Mongolian Tugrik"),
  LAK("Lao Kip"),
  AMD("Armenian Dram"),
  KPW("North Korean Won"),
  MMK("Myanmar Kyat"),
  LRD("Liberian Dollar"),
  BIF("Burundi Franc"),
  HTG("Haitian Gourde"),
  KGS("Kyrgyzstani Som"),
  GNF("Guinean Franc"),
  TMT("Turkmenistani Manat"),
  GEL("Georgian Lari"),
  MDL("Moldovan Leu"),
  CDF("Congolese Franc"),
  SSP("South Sudanese Pound"),
  IQD("Iraqi Dinar"),
  YER("Yemeni Rial"),
  RWF("Rwandan Franc"),
  DJF("Djiboutian Franc"),
  TZS("Tanzanian Shilling"),
  SOS("Somali Shilling"),
  AOA("Angolan Kwanza"),
  MRU("Mauritanian Ouguiya"),
  STN("São Tomean Dobra"),
  CVE("Cape Verdean Escudo"),
  ETB("Ethiopian Birr"),
  BDT("Bangladeshi Taka"),
  ISK("Icelandic Krona"),
  MKD("Macedonian Denar"),
  PAB("Panamanian Balboa"),
  KHR("Cambodian Riel"),
  BGN("Bulgarian Lev"),
  SYP("Syrian Pound"),
  LKR("Sri Lankan Rupee"),
  KZT("Kazakhstani Tenge"),
  BYN("Belarusian Ruble"),
  SLE("Sierra Leonean Leone"),
  DOP("Dominican Peso"),
  AZN("Azerbaijan Manat"),
  BMD("Bermudian Dollar"),
  VES("Venezuelan Bolivar"),
  GYD("Guyanese Dollar"),
  PKR("Pakistani Rupee"),
  NIO("Nicaraguan Cordoba"),
  @JsonEnumDefaultValue UNKNOWN("");

  private final String fullName;
  public static final Set<Currency> ALL_CURRENCIES = unmodifiableSet(complementOf(of(UNKNOWN, EUR)));

  Currency(String fullName) {
    this.fullName = fullName;
  }
}