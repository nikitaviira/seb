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
  MZN("Mozambique Metical"),
  TND("Tunisian Dinar"),
  MAD("Moroccan Dirham"),
  DZD("Algerian Dinar"),
  RSD("Serbian Dinar"),
  LYD("Libyan Dinar"),
  JOD("Jordanian Dinar"),
  MGA("Malagasy Ariary"),
  AFN("Afghan Afghani"),
  TJS("Tajikistan Somoni"),
  XOF("CFA Franc BCEAO"),
  BOB("Bolivian Boliviano"),
  UYU("Peso Uruguayo"),
  UZS("Uzbekistan Som"),
  MNT("Mongolian Tugrik"),
  AMD("Armenian Dram"),
  KGS("Kyrgyzstani Som"),
  GNF("Guinean Franc"),
  TMT("Turkmenistani Manat"),
  GEL("Georgian Lari"),
  MDL("Moldovan Leu"),
  IQD("Iraqi Dinar"),
  YER("Yemeni Rial"),
  DJF("Djiboutian Franc"),
  TZS("Tanzanian Shilling"),
  ETB("Ethiopian Birr"),
  BDT("Bangladeshi Taka"),
  ISK("Icelandic Krona"),
  MKD("Macedonian Denar"),
  PAB("Panamanian Balboa"),
  BGN("Bulgarian Lev"),
  SYP("Syrian Pound"),
  LKR("Sri Lankan Rupee"),
  KZT("Kazakhstani Tenge"),
  BYN("Belarusian Ruble"),
  AZN("Azerbaijan Manat"),
  PKR("Pakistani Rupee"),
  @JsonEnumDefaultValue UNKNOWN("");

  private final String fullName;
  public static final Set<Currency> ALL_CURRENCIES = unmodifiableSet(complementOf(of(UNKNOWN, EUR)));

  Currency(String fullName) {
    this.fullName = fullName;
  }
}