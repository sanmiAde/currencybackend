package com.sanmiaderibigbe.currencybackend

import org.apache.http.client.utils.URIBuilder
import org.jsoup.Jsoup
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception
import java.net.URL

@RestController
class RatingsController {

    @RequestMapping(value = ["/ratings"], method = [RequestMethod.GET])
    fun getCurrentRates(@RequestParam(value = "baseCurrency", defaultValue = "USD") baseCurrency: String,
                        @RequestParam(value = "selectedCurrency") currency: String): ResponseEntity<Ratings> {
        val baseUri = URIBuilder(BASE_SITE)
        baseUri.addParameter("c_input", baseCurrency)
        baseUri.addParameter("cp_input", currency)
        val currentRatingList = extractRatingFromWebsite(baseUri.build().toURL())
        return when {
            currentRatingList.currentratings.isEmpty() -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(currentRatingList)
        }

    }

    private fun extractRatingFromWebsite(currencyRatingsUrl: URL): Ratings {
        val ratings = mutableListOf<Rating>()
        try {
            val site = Jsoup.connect(currencyRatingsUrl.toString()).get()
            val ratingTable = site.getElementsByClass("grid").select("tbody")

            ratingTable.select("tr").forEach {
                var date = it.select("td")[0]
                var rating = it.select("td")[1]
                ratings.add(Rating(date.text(), rating.text()))
            }
        }catch (e : Exception){
            ratings.clear()
        }


        return Ratings(ratings)
    }

    companion object {
        const val BASE_SITE = "https://fx-rate.net/historical/"
    }
}