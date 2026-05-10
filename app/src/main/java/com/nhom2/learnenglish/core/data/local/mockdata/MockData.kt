package com.nhom2.learnenglish.core.data.local.mockdata

import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity

object MockData {
    val articles = listOf(
        ArticleEntity(
            title = "A Saharan Spinosaurus",
            content = "Today, Jenguebi is dry and barren, with few trees and endless sand. The local Tuareg community calls the area where the fossils were found Sirig Taghat, which translates to “No water, no goat.”\n" +
                    "\n" +
                    "Sereno was drawn to the Sahara because of a report by French geologist Hugues Faure in the 1950s of a dinosaur tooth he found in Niger and wanted to search for similar sites.\n" +
                    "\n" +
                    "“I knew it was the needle in the haystack,” Sereno says of the remote fossil site. “It could easily have been swallowed by the sand.”\n" +
                    "\n" +
                    "Led by a local guide named Abdul Nasser riding on a moped across the desert, Sereno and Vidal first scouted the site in 2019 and found a Spinosaurus jawbone, along with a handful of other fossils. After returning in 2022, they ultimately identified bones from three S. mirabilis individuals, along with another predatory dinosaur named Carcharodontosaurus, two long-necked sauropod dinosaurs, crocodiles, turtles, and a freshwater fish species that could reach 12 feet long.",
        ),

        ArticleEntity(
            title = "China develops ultra-low-cost iron battery that retains 99.4% efficiency for over 16 years",
            content = "Chinese scientists have developed an iron battery 80 times cheaper than lithium that can run for over 6,000 cycles with almost no loss in capacity.\n" +
                    "The \"all-iron flow battery\" technology was developed by a research team from the Institute of Metal Research under the Chinese Academy of Sciences, and their findings were published earlier this month in the journal Advanced Energy Materials.\n" +
                    "\n" +
                    "Unlike the lithium-ion batteries commonly used today, all-iron flow batteries rely on iron, which is far cheaper and more widely available, with lithium currently trading at more than 80 times the cost of iron.\n" +
                    "\n" +
                    "The large price gap makes iron-based batteries a possible solution to supply constraints slowing the global shift to green energy, according to Interesting Engineering.\n" +
                    "\n" +
                    "The main challenge for iron flow batteries has been their short lifespan. Over time, the battery’s active materials can break down or leak through internal membranes, reducing efficiency and shortening operational life.\n" +
                    "\n" +
                    "To solve this, the Chinese team designed a new iron compound that acts like a protective shield. It helps prevent damage to the battery’s core materials and stops them from leaking across the membrane, as reported by the South China Morning Post.\n"
        )


    )

    fun generateInitialArticle(): List<ArticleEntity> {
        return articles.map { article ->
            ArticleEntity(title = article.title, content = article.content)
        }
    }
}