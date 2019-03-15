package shiftkun.api

import com.github.swagger.akka.model.Info
import com.github.swagger.akka.SwaggerHttpService

object SwaggerDocApi extends SwaggerHttpService {

  override val apiClasses = Set(
    classOf[SampleApi],
    classOf[UserApi],
  )
  override val host = "http://localhost:8080"
  override val basePath = "/v1"
  override val apiDocsPath = "api-docs"
  override val info = Info(version = "v1", title = "AccountingZ API",
    description =
      """
        |## 新会計システムAPI
        |
        |### APIエラー定義
        |
        |* DB接続エラーなどのアプリケーションの内部的なエラーが発生した場合は、ステータス=500のレスポンスを返す
        |
        |  `(StatusCodes.InternalServerError, ErrorInfo("UnexpectedError", "予期せぬエラーが発生しました"))`
        |
        |* リクエストパス変数やクエリパラメータの型不一致や未指定のエラーが、Akka-HTTPレベルで起きた場合は、以下のレスポンスを返す
        |
        |  (ステータス=404, Content-Type=text/plain, ボディ=The requested resource could not be found.)
        |
        |* リクエストボディ(JSON)のプロパティの型不一致や未指定のエラーが、Akka-HTTPレベルで起きた場合は、以下のレスポンスを返す
        |
        |  (ステータス=400, Content-Type=text/plain, ボディ=The request content was malformed:...)
        |
        ||エラー種別|デフォルトステータスコード|メッセージ|フィールド|
        ||-|-|-|-|
        ||AccountingPeriodNotFound|404|指定された会計期が見つかりません|accountingPeriodId|
        ||JournalNotFound|404|指定された仕訳が見つかりません|journalId|
        ||AccountNotFound|404|指定された科目が見つかりません|accountId|
        ||AccountUnavailable(drCr)|400|指定された科目は利用できません|crAccountId or drAccountId|
        ||InvalidJournalIssuedDate|400|不正な日付です|issuedMonth,issuedDay|
        ||JournalAddFailed(_)|500|仕訳の登録に失敗しました|なし|
        ||InvalidMonth|400|不正な取引月です|issuedMonth|
        ||AccountingPeriodMissMatch(drCr)|400|会計期と仕訳が紐付いていません|crAccountId or drAccountId|
        ||AccountingPeriodNotOpen|400|会計期がOPENではありません|accountingPeriodId|
        ||OverMaxLength(field,length)|400|${label(field)}は${length}文字以下で入力してください|field|
        ||OutOfRange(field, min, max)|400|${label(field)}は${min}以上${max}以下で入力してください|field|
        ||InvalidCharacter(field)|400|s"${label(field)}に使用できない文字が含まれています|field|
        ||JournalTaxNotFound(drCr)|400|指定された課非区分が存在しません|crConsumptionTaxId or drConsumptionTaxId|
        ||JournalTaxRateNotFound(drCr)|400|指定された税率が存在しません|crConsumptionTaxRateId or drConsumptionTaxRateId|
        ||ConsumptionTaxRateForbidden(drCr)|400|課税ではない課非区分で消費税率は設定できません|crConsumptionTaxId or drConsumptionTaxId|
        ||ConsumptionTaxRateNotFound|400|指定された消費税率が存在しません|なし|
        ||ConsumptionTaxRateRequired(drCr)|400|課税の課非区分では消費税率を設定してください|crConsumptionTaxId or drConsumptionTaxId|
        ||InvalidConsumptionTaxRateValue(drCr)|400|課税対象期間より以前の消費税を指定してください|drConsumptionTaxRateId or crConsumptionTaxRateId|
        ||TaxAmountForbidden|400|非課税科目のため税額を設定できません|taxAmount|
        ||TaxAmountGreaterThanAmount|400|内税の場合、税額は金額以下の値で入力してください|taxAmount|
        ||MismatchedTaxRateOnExcludedTax|400|外税の場合、借方貸方で税率の値は一致するようにしてください|taxAmount|
        ||SubAccountNotFound|400|指定された補助科目が見つかりません|subAccountId|
        ||MismatchedSubAccount|400|指定された補助科目は勘定科目に登録されていません|crSubAccountId or drSubAccountId|
        ||SubAccountRequired|400|指定された勘定科目では、補助科目の設定が必要です|crSubAccountId or drSubAccountId|
        ||SubAccountUnavailable|400|指定された補助科目は利用できません|crSubAccountId or drSubAccountId|
        ||InvalidSortCondition|400|不正なソート条件です|sort|
        ||JournalNotActive|400|指定された仕訳はActiveではありません|journalStatus|
        ||上記以外|500|予期せぬエラーが発生しました|なし|
        |
        |""".stripMargin)
}
