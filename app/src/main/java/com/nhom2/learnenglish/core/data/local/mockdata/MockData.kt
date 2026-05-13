package com.nhom2.learnenglish.core.data.local.mockdata

import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity
import com.nhom2.learnenglish.core.data.local.entity.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.WordSetEntity

object MockData {

    val wordSets = listOf(
        WordSetEntity(id = 1, name = "Giao tiếp cơ bản", description = "Các câu chào hỏi và từ vựng thông dụng hàng ngày.", unlockCost = 0),
        WordSetEntity(id = 2, name = "Công nghệ thông tin", description = "Từ vựng chuyên ngành lập trình và phần cứng.", unlockCost = 100),
        WordSetEntity(id = 3, name = "Kinh doanh & Tài chính", description = "Từ vựng về hợp đồng, đầu tư và đàm phán.", unlockCost = 200),
        WordSetEntity(id = 4, name = "Du lịch & Khám phá", description = "Hành lý, sân bay và các địa điểm tham quan.", unlockCost = 50)
    )

    val words = listOf(
        WordEntity(id = 1, englishWord = "Hello", vietnameseMeaning = "Xin chào", audio = "hello_audio_url"),
        WordEntity(id = 2, englishWord = "Thank you", vietnameseMeaning = "Cảm ơn", audio = "thanks_audio_url"),
        WordEntity(id = 3, englishWord = "Please", vietnameseMeaning = "Vui lòng / Làm ơn", audio = "please_audio_url"),
        WordEntity(id = 4, englishWord = "Sorry", vietnameseMeaning = "Xin lỗi", audio = "sorry_audio_url"),
        WordEntity(id = 5, englishWord = "Goodbye", vietnameseMeaning = "Tạm biệt", audio = "goodbye_audio_url"),

        WordEntity(id = 6, englishWord = "Database", vietnameseMeaning = "Cơ sở dữ liệu", audio = "db_audio_url"),
        WordEntity(id = 7, englishWord = "Algorithm", vietnameseMeaning = "Thuật toán", audio = "algo_audio_url"),
        WordEntity(id = 8, englishWord = "Application", vietnameseMeaning = "Ứng dụng", audio = "app_audio_url"),
        WordEntity(id = 9, englishWord = "Variable", vietnameseMeaning = "Biến số", audio = "var_audio_url"),
        WordEntity(id = 10, englishWord = "Server", vietnameseMeaning = "Máy chủ", audio = "server_audio_url"),

        WordEntity(id = 11, englishWord = "Contract", vietnameseMeaning = "Hợp đồng", audio = "contract_audio_url"),
        WordEntity(id = 12, englishWord = "Investment", vietnameseMeaning = "Đầu tư", audio = "invest_audio_url"),
        WordEntity(id = 13, englishWord = "Negotiation", vietnameseMeaning = "Đàm phán", audio = "nego_audio_url"),
        WordEntity(id = 14, englishWord = "Profit", vietnameseMeaning = "Lợi nhuận", audio = "profit_audio_url"),
        WordEntity(id = 15, englishWord = "Bankruptcy", vietnameseMeaning = "Phá sản", audio = "bankrupt_audio_url")
    )

    val wordSetRefs = listOf(
        WordSetCrossRef(1, 1), WordSetCrossRef(2, 1), WordSetCrossRef(3, 1), WordSetCrossRef(4, 1), WordSetCrossRef(5, 1),
        WordSetCrossRef(6, 2), WordSetCrossRef(7, 2), WordSetCrossRef(8, 2), WordSetCrossRef(9, 2), WordSetCrossRef(10, 2),
        WordSetCrossRef(11, 3), WordSetCrossRef(12, 3), WordSetCrossRef(13, 3), WordSetCrossRef(14, 3), WordSetCrossRef(15, 3)
    )
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
            level = "B2 Intermediate",
            category = "Business",
            readTime = "5 min read",
            isCompleted = true
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
                    "To solve this, the Chinese team designed a new iron compound that acts like a protective shield. It helps prevent damage to the battery’s core materials and stops them from leaking across the membrane, as reported by the South China Morning Post.\n",

            level = "B2 Intermediate",
            category = "Business",
            readTime = "5 min read",
            isCompleted = false
        )
    )

}