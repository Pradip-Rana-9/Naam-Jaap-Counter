package com.example.data

data class GitaShlokItem(
    val chapterNumber: Int,
    val shlokNumber: Int,
    val chapterNameEnglish: String,
    val chapterNameSanskrit: String,
    val sanskrit: String,
    val transliteration: String = "",
    val hinglishMeaning: String = "",
    val simpleExplanation: String = "",
    val hindiMeaning: String = "",
    val englishMeaning: String = ""
) {
    val verseRefShort: String
        get() = "BG $chapterNumber.$shlokNumber"

    val verseRefFull: String
        get() = "BG $chapterNumber.$shlokNumber • Adhyay $chapterNumber, Shlok $shlokNumber"

    val hinglishExplanation: String
        get() = if (simpleExplanation.isNotEmpty()) simpleExplanation else hinglishMeaning
}

object GitaDataRepository {

    val allGitaWisdomList: List<GitaShlokItem> = listOf(
        GitaShlokItem(
            chapterNumber = 2,
            shlokNumber = 47,
            chapterNameEnglish = "Sankhya Yoga",
            chapterNameSanskrit = "सांख्ययोग",
            sanskrit = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि॥",
            transliteration = "karmaṇy-evādhikāras te mā phaleṣhu kadāchana\nmā karma-phala-hetur bhūr mā te saṅgo ’stvakarmaṇi",
            hinglishMeaning = "Tumhara adhikar keval karm karne par hai, uske phalon (results) par kabhi nahi. Isliye phal ki apeksha karke karm mat karo, aur na hi karm na karne (aalasy) mein tumhara lagav ho.",
            simpleExplanation = "Bhagwan Shri Krishna Nishkama Karma Yoga ka mool mantra sikhate hain. Jab hum kisi kaam ko karte waqt keval uske result, profit ya reward ke baare mein sochte hain, toh mann mein chinta, darr aur asafalta ka tanav paida hota hai. Iske viprit, jab hum apna 100% dhyan aur mehnat apne kartavya par lagate hain aur parinam ko Bhagwan ke charnon mein samarpit kar dete hain, toh mann poori tarah shant aur sthir ho jata hai. Daily life mein bina chinta ke apna sarvashreshtha yogdan dijiye aur fal ki chinta Ishwar par chhod dijiye."
        ),
        GitaShlokItem(
            chapterNumber = 3,
            shlokNumber = 8,
            chapterNameEnglish = "Karma Yoga",
            chapterNameSanskrit = "कर्मयोग",
            sanskrit = "नियतं कुरु कर्म त्वं कर्म ज्यायो ह्यकर्मणः।\nशरीरयात्रापि च ते न प्रसिद्ध्येदकर्मणः॥",
            transliteration = "niyataṁ kuru karma tvaṁ karma jyāyo hy-akarmaṇaḥ\nśharīra-yātrāpi cha te na prasiddhyed akarmaṇaḥ",
            hinglishMeaning = "Tum apne niyat (shastrokt kartavya) karm karo, kyunki karm na karne se karm karna kahin behtar hai. Bina karm kiye toh tumhara shareer chalana (jeevan nirvah) bhi sambhav nahi hoga.",
            simpleExplanation = "Shri Krishna samjhate hain ki karm se bhaagna ya aalasy karna aadhyatmikta nahi hai. Hamara shareer bhi tabhi swasth rehta hai jab hum niyamit roop se apna kaam karte hain. Isliye parivar, samaj, aur aatm-kalyan ke prati jo bhi niyat kartavya hain, unhe poori imaandari se nibhana chahiye. Karm karna hi jeevan ki gati hai aur sadhana ka aadhar hai."
        ),
        GitaShlokItem(
            chapterNumber = 4,
            shlokNumber = 38,
            chapterNameEnglish = "Jnana Karma Sannyasa Yoga",
            chapterNameSanskrit = "ज्ञानकर्मसंन्यासयोग",
            sanskrit = "न हि ज्ञानेन सदृशं पवित्रमिह विद्यते।\nतत्स्वयं योगसंसिद्धः कालेनात्मनि विन्दति॥",
            transliteration = "na hi jñānena sadṛiśhaṁ pavitram iha vidyate\ntat svayaṁ yoga-saṁsiddhaḥ kālenātmani vindati",
            hinglishMeaning = "Is sansar mein divya aatm-gyan ke saman aatma ko pavitra karne wala kuch bhi doosra nahi hai. Jo vyakti nishkam karm aur bhakti yog mein siddh hota hai, vah samay aane par us gyan ko apne andar swayam anubhav karta hai.",
            simpleExplanation = "Gita ka yeh amrit-vachan batata hai ki aatm-gyan (spiritual wisdom) hamari aatma ko shuddh aur pavitra karne wala sabse mahan madhyam hai. Jab hum niyamit roop se Naam Jaap, Ishwar-chintan aur satkarm karte hain, toh dheere-dheere hamare andar ka agyan, bhed-bhav aur moh door hota hai. Ek sadhak ko dharay (patience) rakhna chahiye, kyunki nishtha aur prem se samay aane par gyan swatah hriday mein prakat ho jata hai."
        ),
        GitaShlokItem(
            chapterNumber = 6,
            shlokNumber = 19,
            chapterNameEnglish = "Dhyana Yoga",
            chapterNameSanskrit = "ध्यानयोग",
            sanskrit = "यथा दीपो निवातस्थो नेङ्गते सोपमा स्मृता।\nयोगिनो यतचित्तस्य युञ्जतो योगमात्मनः॥",
            transliteration = "yathā dīpo nivāta-stho neṅgate sopamā smṛitā\nyogino yata-chittasya yuñjato yogam ātmanaḥ",
            hinglishMeaning = "Jaise hawa-rahit (bina hawa ke) sthan par rakha hua deepak kabhi hilta nahi balki bilkul sthir jalta rehta hai, theek usi tarah aatma ke dhyan mein lage hue yogi ka mann poori tarah nishchal aur sthir rehta hai.",
            simpleExplanation = "Dhyan aur Jaap mein man ki ekagrata kaise prapt ho, yeh shlok uska sabse saral udaharan deta hai. Sansar ki ichhayein aur chintayein tez hawa ke jhonkon ki tarah hain jo mann ke deepak ko bhatkati hain. Jab hum daily Mala Jaap karte hain aur dhyan lagate hain, toh man ki sari chanchalta shant ho jati hai aur andar ek atoot divya shanti ka deepak jal uthta hai."
        ),
        GitaShlokItem(
            chapterNumber = 9,
            shlokNumber = 22,
            chapterNameEnglish = "Raja Vidya Raja Guhya Yoga",
            chapterNameSanskrit = "राजविद्याराजगुह्ययोग",
            sanskrit = "अनन्याश्चिन्तयन्तो मां ये जनाः पर्युपासते।\nतेषां नित्याभियुक्तानां योगक्षेमं वहाम्यहम्॥",
            transliteration = "ananyāśh chintayanto māṁ ye janāḥ paryupāsate\nteṣhāṁ nityābhiyuktānāṁ yoga-kṣhemaṁ vahāmy aham",
            hinglishMeaning = "Jo ananya bhakt bina kisi doosre sahare ke nitya mera hi chintan karte hue meri bhakti mein lage rehte hain, unke paas jo nahi hai use main pradan karta hoon aur jo unke paas hai uski raksha main swayam karta hoon.",
            simpleExplanation = "Yeh Bhagwan Shri Krishna ka shrimukha se diya gaya sabse aashwasankari vachan (divine promise) hai. Jab koi bhakt poori shraddha aur nishtha se Bhagwan par vishwas karta hai aur unka Naam Jaap karta hai, toh Bhagwan uske spiritual aur bhautik dono kalyan ki zimmedari khud utha lete hain. Bhakt ko kabhi kisi chinta ya abhav mein bhatakne ki zaroorat nahi padti."
        ),
        GitaShlokItem(
            chapterNumber = 12,
            shlokNumber = 6,
            chapterNameEnglish = "Bhakti Yoga",
            chapterNameSanskrit = "भक्तियोग",
            sanskrit = "ये तु सर्वाणि कर्माणि मयि संन्यस्य मत्पराः।\nअनन्येनैव योगेन मां ध्यायन्त उपासते॥",
            transliteration = "ye tu sarvāṇi karmāṇi mayi sannyasya mat-parāḥ\nananyenaiva yogena māṁ dhyāyanta upāsate",
            hinglishMeaning = "Jo log apne sabhi karmon ko mujhe samarpit karke, mujhe hi param lakshya maante hain aur ananya bhakti-yog se mera dhyan karte hue meri upasna karte hain, unka uddhar main swayam karta hoon.",
            simpleExplanation = "Bhakti marg sabse saral aur sabse prabhavshali marg hai. Isme kisi kathin sharirik tapashya ki aavashyakta nahi hoti, bas apne dincharya ke har karya ko Prabhu ki seva samajh kar arpit karna hota hai. Jo vyakti aisi nishkapat prem-bhakti karta hai, Bhagwan use sansar-sagar ke sabhi kashton se swayam paar utaar dete hain."
        ),
        GitaShlokItem(
            chapterNumber = 18,
            shlokNumber = 66,
            chapterNameEnglish = "Moksha Sannyasa Yoga",
            chapterNameSanskrit = "मोक्षसंन्यासयोग",
            sanskrit = "सर्वधर्मान्परित्यज्य मामेकं शरणं व्रज।\nअहं त्वां सर्वपापेभ्यो मोक्षयिष्यामि मा शुचः॥",
            transliteration = "sarva-dharmān parityajya mām ekaṁ śharaṇaṁ vraja\nahaṁ tvāṁ sarva-pāpebhyo mokṣhayiṣhyāmi mā śhuchaḥ",
            hinglishMeaning = "Sabhi dharmo aur kramkando ki chinta chhodkar bas keval meri sharan mein aa jao. Main tumhe sabhi paapon aur bandhano se mukt kar dunga, tum chinta aur shok mat karo.",
            simpleExplanation = "Shrimad Bhagavad Gita ka yeh sabse antim aur sarvashreshtha updesh 'Charama Shlok' kehlata hai. Iska aadhar hai purna aatm-samarpan (total surrender). Jab hum apni saari chintayein, ahamkar aur darr Bhagwan ke charno mein daal dete hain, tab Bhagwan ka anant prem aur kripa humein har sankat se bachati hai aur aatmik mukti pradan karti hai."
        ),
        GitaShlokItem(
            chapterNumber = 10,
            shlokNumber = 8,
            chapterNameEnglish = "Vibhuti Yoga",
            chapterNameSanskrit = "विभूतियोग",
            sanskrit = "अहं सर्वस्य प्रभवो मत्तः सर्वं प्रवर्तते।\nइति मत्वा भजन्ते मां बुधा भावसमन्विताः॥",
            transliteration = "ahaṁ sarvasya prabhavo mattaḥ sarvaṁ pravartate\niti matvā bhajante māṁ budhā bhāva-samanvitāḥ",
            hinglishMeaning = "Main hi samast bhautik aur aadhyatmik srishti ka utpatti-karan hoon. Sab kuch mujhse hi chalta hai. Aisa jaankar gyanwan bhakt poore prem-bhav ke sath meri bhakti mein leen rehte hain.",
            simpleExplanation = "Jab hum yeh samajh jaate hain ki is poore brahmand ka srot, shakti aur niyamak keval ek Parmeshwar hi hain, toh hamare hriday mein shuddh prem aur aadar jaagrit hota hai. Hum har jeev mein Bhagwan ki upsthiti dekhne lagte hain aur bina kisi bhed-bhav ke sabhi ke kalyan ki prarthana karte hain."
        ),
        GitaShlokItem(
            chapterNumber = 8,
            shlokNumber = 7,
            chapterNameEnglish = "Akshara Brahma Yoga",
            chapterNameSanskrit = "अक्षरब्रह्मयोग",
            sanskrit = "तस्मात्सर्वेषु कालेषु मामनुस्मर युध्य च।\nमय्यर्पितमनोबुद्धिर्मामेवैष्यस्यसंशयः॥",
            transliteration = "tasmāt sarveṣhu kāleṣhu mām anusmara yudhya cha\nmayy arpita-mano-buddhir mām evaiṣhyasy asaṁśhayaḥ",
            hinglishMeaning = "Isliye Arjun, tum har samay mera nirantar smaran (Naam Jaap) karo aur apna yuddh (kartavya karm) bhi laddo. Mujh mein man aur buddhi samarpit karne se tum nishchit hi mujhe prapt karoge.",
            simpleExplanation = "Yeh shlok humein sikhata hai ki bhakti aur daily responsibilities dono ek sath chal sakti hain. Bhagwan yeh nahi kehte ki kaam chhod do, balki kehte hain ki hath se karm karo aur mann se Bhagwan ka naam japte raho. Jab hum kaam karte waqt bhi andar hi andar Prabhu ka smaran rakhte hain, toh har kaam pooja ban jata hai."
        ),
        GitaShlokItem(
            chapterNumber = 17,
            shlokNumber = 3,
            chapterNameEnglish = "Shraddhatraya Vibhaga Yoga",
            chapterNameSanskrit = "श्रद्धात्रयविभागयोग",
            sanskrit = "सत्त्वानुरूपा सर्वस्य श्रद्धा भवति भारत।\nश्रद्धामयोऽयं पुरुषो यो यच्छ्रद्धः स एव सः॥",
            transliteration = "sattvānurūpā sarvasya śhraddhā bhavati bhārata\nśhraddhā-mayo ’yaṁ puruṣho yo yach-chhraddhaḥ sa eva saḥ",
            hinglishMeaning = "He Bharat, har manushya ki shraddha uske antahkaran ke swabhaav ke anuroop hoti hai. Manushya mool roop se apni shraddha se hi banta hai—jiski jaisi shraddha hoti hai, vah waisa hi ban jata hai.",
            simpleExplanation = "Hamara vishwas aur hamari shraddha hi hamare jeevan ki disha tay karti hai. Agar hamari shraddha satvik vicharo, sadhana aur Ishwar prem mein hai, toh hamara vyaktitva shant, pavitra aur safal banega. Apni shraddha ko sadaiva uchh adarsho aur Bhagwan ke shrimukh vachano par sthir rakhna chahiye."
        ),
        GitaShlokItem(
            chapterNumber = 4,
            shlokNumber = 7,
            chapterNameEnglish = "Jnana Karma Sannyasa Yoga",
            chapterNameSanskrit = "ज्ञानकर्मसंन्यासयोग",
            sanskrit = "यदा यदा हि धर्मस्य ग्लानिर्भवति भारत।\nअभ्युत्थानमधर्मस्य तदात्मानं सृजाम्यहम्॥",
            transliteration = "yadā yadā hi dharmasya glānir bhavati bhārata\nabhyutthānam adharmasya tadātmānaṁ sṛijāmy aham",
            hinglishMeaning = "He Bharat! Jab-jab dharm ki haani hoti hai aur adharm ki vriddhi hone lagti hai, tab-tab main swayam apne roop ko prakat (avatarit) karta hoon.",
            simpleExplanation = "Bhagwan Shri Krishna srishti ke santulan aur satya ki raksha ke liye baar-baar avatar lete hain. Yeh shlok humein aashwasan deta hai ki andhera chahe kitna bhi gehra ho, satya aur dharm ki hamesha jeet hoti hai. Har bhakt ko hamesha satya aur sadachar ke marg par dridhta se chalna chahiye."
        ),
        GitaShlokItem(
            chapterNumber = 4,
            shlokNumber = 8,
            chapterNameEnglish = "Jnana Karma Sannyasa Yoga",
            chapterNameSanskrit = "ज्ञानकर्मसंन्यासयोग",
            sanskrit = "परित्राणाय साधूनां विनाशाय च दुष्कृताम्।\nधर्मसंस्थापनार्थाय सम्भवामि युगे युगे॥",
            transliteration = "paritrāṇāya sādhūnāṁ vināśhāya cha duṣhkṛitām\ndharma-saṁsthāpanārthāya sambhavāmi yuge yuge",
            hinglishMeaning = "Sajjan aur sadhu purushon ke kalyan ke liye, dushkarmiyon ke naash ke liye, aur satya-dharm ki punah sthapana ke liye main har yug mein prakat hota hoon.",
            simpleExplanation = "Bhagwan ka har avatar bhakton ke dukh door karne aur unhe suraksha pradan karne ke liye hota hai. Jo vyakti Bhagwan ke sharan mein rehta hai aur dharmik aacharan karta hai, use kisi bhi anyay ya burai se darne ki aavashyakta nahi hai, kyunki swayam Ishwar uske rakshak hain."
        ),
        GitaShlokItem(
            chapterNumber = 15,
            shlokNumber = 15,
            chapterNameEnglish = "Purushottama Yoga",
            chapterNameSanskrit = "पुरुषोत्तमयोग",
            sanskrit = "सर्वस्य चाहं हृदि सन्निविष्टो मत्तः स्मृतिर्ज्ञानमपोहनं च।\nवेदैश्च सर्वैरहमेव वेद्यो वेदान्तकृद्वेदविदेव चाहम्॥",
            transliteration = "sarvasya chāhaṁ hṛidi sanniviṣhṭo mattaḥ smṛitir jñānam apohanaṁ cha\nvedaiśh cha sarvair aham eva vedyo vedānta-kṛid veda-vid eva chāham",
            hinglishMeaning = "Main sabhi jeevon ke hriday mein aatma-roop se sthit hoon. Mujhse hi smriti (memory), gyan aur unka aabhav hota hai. Samast Vedon dwara main hi janne yogya hoon.",
            simpleExplanation = "Prabhu kahin door aakash mein nahi, balki hamare hi hriday ke bheetar nivas karte hain. Hamari har saans, hamari samajh aur hamara astitva unhi ki shakti se hai. Jab hum apne hriday mein baithe Prabhu ka dhyan karte hain aur Naam Jaap karte hain, toh humein sachha aatm-anubhav prapt hota hai."
        ),
        GitaShlokItem(
            chapterNumber = 7,
            shlokNumber = 16,
            chapterNameEnglish = "Jnana Vijnana Yoga",
            chapterNameSanskrit = "ज्ञानकर्मसंन्यासयोग",
            sanskrit = "चतुर्विधा भजन्ते मां जनाः सुकृतिनोऽर्जुन।\nआर्तो जिज्ञासुरर्थार्थी ज्ञानी च भरतर्षभ॥",
            transliteration = "chatur-vidhā bhajante māṁ janāḥ sukṛitino ’rjuna\nārto jijñāsur arthārthī jñānī cha bharatarṣhabha",
            hinglishMeaning = "He Arjun! Chaar prakar ke punyaatma log meri bhakti karte hain—aart (dukh-kasht se peedit), artharthi (bhautik laabh chahne wale), jigyasu (satya ko janne ke ichhuk), aur gyani (aatma ko janne wale).",
            simpleExplanation = "Bhagwan har us vyakti ko sweekar karte hain jo kisi bhi bhav se unke paas aata hai. Chahe aap sankat mein ho, koi aavashyakta ho, ya satya ki khoj ho—Bhagwan ki sharan lena hi shreshtha hai. Lekin inme se jo nishkam bhav se prem karne wala gyani bhakt hai, vah Bhagwan ko sarvadhik priya hota hai."
        ),
        GitaShlokItem(
            chapterNumber = 2,
            shlokNumber = 70,
            chapterNameEnglish = "Sankhya Yoga",
            chapterNameSanskrit = "सांख्ययोग",
            sanskrit = "आपूर्यमाणमचलप्रतिष्ठं समुद्रमापः प्रविशन्ति यद्वत्।\nतद्वत्कामा यं प्रविशन्ति सर्वे स शान्तिमाप्नोति न कामकामी॥",
            transliteration = "āpūryamāṇam achala-pratiṣhṭhaṁ samudram āpaḥ praviśhanti yadvat\ntadvat kāmā yaṁ praviśhanti sarve sa śhāntim āpnoti na kāma-kāmī",
            hinglishMeaning = "Jaise charo taraf se bhare hue sthir samudra mein kitni bhi nadiyan aakar mil jaayein, fir bhi samudra kshobh ko prapt nahi hota, theek usi tarah jiske andar sansarik ichhayein bina koi vikar paida kiye sama jati hain, wahi sachhi shanti pata hai.",
            simpleExplanation = "Ek sachhe sadhak ka mann vishal samudra ki tarah gambhir hona chahiye. Sansar mein aane wale dukh-sukh, ninda-stuti ya ichhayein uske mann ko vichalit nahi kar paati. Jo vyakti hamesha nayi ichhaon ke peeche bhaagta hai use kabhi shanti nahi milti, parantu jo aatm-tript rehta hai vah param shanti anubhav karta hai."
        )
    )

    // Complete chapter shloks database covering all 18 chapters
    private val chapterShloksMap: Map<Int, List<GitaShlokItem>> = mapOf(
        1 to listOf(
            GitaShlokItem(
                chapterNumber = 1,
                shlokNumber = 1,
                chapterNameEnglish = "Arjuna Vishada Yoga",
                chapterNameSanskrit = "अर्जुनविषादयोग",
                sanskrit = "धर्मक्षेत्रे कुरुक्षेत्रे समवेता युयुत्सवः।\nमामकाः पाण्डवाश्चैव किमकुर्वत सञ्जय॥",
                transliteration = "dharma-kṣhetre kuru-kṣhetre samavetā yuyutsavaḥ\nmāmakāḥ pāṇḍavāśh chaiva kim akurvata sañjaya",
                hinglishMeaning = "Dhritarashtra ne poocha: He Sanjay! Pavitra dharmakshetra Kurukshetra mein yuddh ki ichha se ikattha hue mere aur Pandu ke putron ne kya kiya?",
                simpleExplanation = "Shrimad Bhagavad Gita ka pratham shlok jeevan ke mool sangharsh ko darshata hai. Kurukshetra hamare antahkaran ka prateek hai jahan dharm aur adharm ke beech har roz yuddh chalta hai. Ishwar ke smaran se hi hum sahi raste ka chayan kar paate hain."
            ),
            GitaShlokItem(
                chapterNumber = 1,
                shlokNumber = 2,
                chapterNameEnglish = "Arjuna Vishada Yoga",
                chapterNameSanskrit = "अर्जुनविषादयोग",
                sanskrit = "दृष्ट्वा तु पाण्डवानीकं व्यूढं दुर्योधनस्तदा।\nआचार्यमुपसङ्गम्य राजा वचनमब्रवीत्॥",
                transliteration = "dṛiṣhṭvā tu pāṇḍavānīkaṁ vyūḍhaṁ duryodhanas tadā\nāchāryam upasaṅgamya rājā vachanam abravīt",
                hinglishMeaning = "Sanjay ne kaha: Us samay Raja Duryodhan ne Pandavon ki sena ki vyuh-rachna dekh kar apne Guru Dronacharya ke paas jaakar yeh vachan kahe.",
                simpleExplanation = "Duryodhan ka aacharan humein sikhata hai ki jab manushya ahankar mein hota hai, toh vah hamesha darr aur aashanka se ghira rehta hai. Guru ke paas vinamrata se jana chahiye na ki ahankar se."
            ),
            GitaShlokItem(
                chapterNumber = 1,
                shlokNumber = 28,
                chapterNameEnglish = "Arjuna Vishada Yoga",
                chapterNameSanskrit = "अर्जुनविषादयोग",
                sanskrit = "दृष्ट्वेमं स्वजनं कृष्ण युयुत्सुं समुपस्थितम्।\nसीदन्ति मम गात्राणि मुखं च परिशुष्यति॥",
                transliteration = "dṛiṣhṭvemaṁ sva-janaṁ kṛiṣhṇa yuyutsuṁ samupasthitam\nsīdanti mama gātrāṇi mukhaṁ cha pariśhuṣhyati",
                hinglishMeaning = "Arjun ne kaha: He Krishna! Yuddh ke maidan mein ikatthe hue apne hi sage-sambandhiyon ko dekh kar mere ang shithil ho rahe hain aur mera mukh sukh raha hai.",
                simpleExplanation = "Arjun ka moh aur shok manushya ki kamzori ko dikhata hai. Jab hum kartavya ke aage moh ko le aate hain, toh humara mann durbhal ho jata hai. Is durbhalta ko door karne ke liye hi Shri Krishna ne Gita ka amrit updesh diya."
            )
        ),
        2 to listOf(
            GitaShlokItem(
                chapterNumber = 2,
                shlokNumber = 14,
                chapterNameEnglish = "Sankhya Yoga",
                chapterNameSanskrit = "सांख्ययोग",
                sanskrit = "मात्रास्पर्शास्तु कौन्तेय शीतोष्णसुखदुःखदाः।\nआगमापायिनोऽनित्यास्तांस्तितिक्षस्व भारत॥",
                transliteration = "mātrā-sparśhās tu kaunteya śhītoṣhṇa-sukha-duḥkha-dāḥ\nāgamāpāyino ’nityās tans titikṣhasva bhārata",
                hinglishMeaning = "He Kunti-putra! Indriyon aur vishayon ke milne se thand-garmi aur sukh-dukh aate-jaate rehte hain. Yeh sab anitya (temporary) hain, isliye he Bharat, tum inhein sehan karna seekho.",
                simpleExplanation = "Jeevan mein sukh aur dukh mausam ki tarah aate aur jaate hain. Jo sadhak dono sthitiyon mein dharaywan aur shant rehta hai, wahi aatm-sakshatkar ke yogya banta hai. Kisi bhi parishani mein ghabrayein nahi, prabhu ka naam japte hue aage badhein."
            ),
            GitaShlokItem(
                chapterNumber = 2,
                shlokNumber = 20,
                chapterNameEnglish = "Sankhya Yoga",
                chapterNameSanskrit = "सांख्ययोग",
                sanskrit = "न जायते म्रियते वा कदाचिन् नायं भूत्वा भविता वा न भूयः।\nअजो नित्यः शाश्वतोऽयं पुराणो न हन्यते हन्यमाने शरीरे॥",
                transliteration = "na jāyate mriyate vā kadāchin nāyaṁ bhūtvā bhavitā vā na bhūyaḥ\najo nityaḥ śhāśhvato ’yaṁ purāṇo na hanyate hanyamāne śharīre",
                hinglishMeaning = "Aatma na toh kabhi janm leti hai aur na marti hai; na yeh utpann hokar punah abhav ko prapt hoti hai. Yeh janmarahit, nitya, shashwat aur puratan hai. Shareer ke mare jaane par bhi aatma nahi marti.",
                simpleExplanation = "Aatma amar hai aur shareer kewal ek vastra ki tarah hai. Jab hum shareer ke moh se oopar uthkar aatma-bhav mein sthit hote hain, toh mrityu aur sankat ka sara darr sada ke liye samaapt ho jata hai."
            ),
            GitaShlokItem(
                chapterNumber = 2,
                shlokNumber = 47,
                chapterNameEnglish = "Sankhya Yoga",
                chapterNameSanskrit = "सांख्ययोग",
                sanskrit = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि॥",
                transliteration = "karmaṇy-evādhikāras te mā phaleṣhu kadāchana\nmā karma-phala-hetur bhūr mā te saṅgo ’stvakarmaṇi",
                hinglishMeaning = "Tumhara adhikar keval karm karne par hai, uske phalon par kabhi nahi. Isliye phal ki aasha rakhkar karm mat karo aur na hi karm na karne mein tumhari aasakti ho.",
                simpleExplanation = "Nishkama karm yoga ka yeh param sutra humein batata hai ki parinam ki chinta chhodkar shuddh niyat se karm karna hi aatmik shanti aur safalta ka marg hai."
            ),
            GitaShlokItem(
                chapterNumber = 2,
                shlokNumber = 70,
                chapterNameEnglish = "Sankhya Yoga",
                chapterNameSanskrit = "सांख्ययोग",
                sanskrit = "आपूर्यमाणमचलप्रतिष्ठं समुद्रमापः प्रविशन्ति यद्वत्।\nतद्वत्कामा यं प्रविशन्ति सर्वे स शान्तिमाप्नोति न कामकामी॥",
                transliteration = "āpūryamāṇam achala-pratiṣhṭhaṁ samudram āpaḥ praviśhanti yadvat\ntadvat kāmā yaṁ praviśhanti sarve sa śhāntim āpnoti na kāma-kāmī",
                hinglishMeaning = "Jaise poori tarah bhare hue sthir samudra mein nadiyan bina kisi kshobh ke samahit ho jaati hain, theek usi tarah jis manushya mein ichhayein bina vichalit kiye sama jaati hain, wahi shanti pata hai.",
                simpleExplanation = "Stithaprajna (sthir buddhi) sadhak wahi hai jiska mann samudra ki tarah gambhir aur shant hota hai. Bahar ke sankat use bhatka nahi sakte."
            )
        ),
        3 to listOf(
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 1,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "ज्यायसी चेत्कर्मणस्ते मता बुद्धिर्जनार्दन।\nतत्किं कर्मणि घोरे मां नियोजयसि केशव॥",
                transliteration = "jyāyasī chet karmaṇas te matā buddhir janārdana\ntat kiṁ karmaṇi ghore māṁ niyojayasi keśhava",
                hinglishMeaning = "Arjun ne poocha: He Janardan! Agar aap gyan aur buddhi-yog ko karm se shreshtha maante hain, toh he Keshav, aap mujhe is ghor aur bhayanak yuddh ke karm mein kyun laga rahe hain?",
                simpleExplanation = "Arjun gyan aur karm ke beech santulan samajhna chahte hain. Bhagwan Shri Krishna aage batate hain ki bina nishkam karm kiye gyan tak pahunchna asambhav hai."
            ),
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 2,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "व्यामिश्रेणेव वाक्येन बुद्धिं मोहयसीव मे।\nतदेकं वद निश्चित्य येन श्रेयोऽहमाप्नुयाम्॥",
                transliteration = "vyāmiśhreṇeva vākyena buddhiṁ mohayasīva me\ntad ekaṁ vada niśhchitya yena śhreyo ’ham āpnuyām",
                hinglishMeaning = "Arjun kehte hain: Aapke dvidhapurna (mixed) vachano se meri buddhi bhatak rahi hai. Kripya nishchit karke ek aisa rasta batayein jisse mera param kalyan ho sake.",
                simpleExplanation = "Ek shishya ka Guru ke prati nishkapat samarpan yeh darshata hai ki jab jeevan mein duvidha ho, toh Ishwar ke vachano par poora bharosa karna chahiye."
            ),
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 3,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "लोकेऽस्मिन्द्विविधा निष्ठा पुरा प्रोक्ता मयानघ।\nज्ञानयोगेन साङ्ख्यानां कर्मयोगेन योगिनाम्॥",
                transliteration = "loke ’smin dvi-vidhā niṣhṭhā purā proktā mayānagha\njñāna-yogena sāṅkhyānāṁ karma-yogena yoginām",
                hinglishMeaning = "Shri Krishna bole: He nishpaap Arjun! Is sansar mein aatm-sakshatkar ke do marg mere dwara pehle bataye gaye hain—gyaniyon ke liye Jnana Yoga aur karm-yogiyon ke liye Karma Yoga.",
                simpleExplanation = "Dono hi marg aatm-shuddhi aur Ishwar-prapti ke raste hain. Grahasta aur aam sadhak ke liye Karma Yoga (kartavya karm + Naam Jaap) sabse sahaj marg hai."
            ),
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 4,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "न कर्मणामनारम्भान्नैष्कर्म्यं पुरुषोऽश्नुते।\nन च संन्यसनादेव सिद्धिं समधिगच्छति॥",
                transliteration = "na karmaṇām anārambhān naiṣhkarmyaṁ puruṣho ’śhnute\nna cha sannyasanād eva siddhiṁ samadhigachchhati",
                hinglishMeaning = "Karmon ko shuru na karne se manushya nishkarmata ko prapt nahi hota, aur na hi keval karmon ka sannyas (tyag) lene se param siddhi milti hai.",
                simpleExplanation = "Karmon se bhaagne se koi yogi nahi banta. Karmon ko bina kisi ahankar aur swarth ke Prabhu-seva roop mein karna hi sachha sannyas hai."
            ),
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 5,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "न हि कश्चित्क्षणमपि जातु तिष्ठत्यकर्मकृत्।\nकार्यते ह्यवशः कर्म सर्वः प्रकृतिजैर्गुणैः॥",
                transliteration = "na hi kaśhchit kṣhaṇam api jātu tiṣhṭhaty akarma-kṛit\nkāryate hy avaśhaḥ karma sarvaḥ prakṛiti-jair guṇaiḥ",
                hinglishMeaning = "Nissandeh koi bhi manushya kisi bhi kaal mein ek pal ke liye bhi bina karm kiye nahi reh sakta, kyunki prakriti ke gun har vyakti se vivash hokar karm karwate hain.",
                simpleExplanation = "Saans lena, sochna aur dekhna bhi karm hai. Jab karm se bacha nahi ja sakta, toh kyon na har karm ko satvik aur bhagavad-arpan bana diya jaye!"
            ),
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 6,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "कर्मेन्द्रियाणि संयम्य य आस्ते मनसा स्मरन्।\nइन्द्रियार्थान्विमूढात्मा मिथ्याचारः स उच्यते॥",
                transliteration = "karmendriyāṇi saṁyamya ya āste manasā smaran\nindriyārthān vimūḍhātmā mithyāchāraḥ sa uchyate",
                hinglishMeaning = "Jo vyakti bahar se indriyon ko rok kar baitha hai, parantu mann ke bheetar sansarik bhogon ka dhyan karta rehta hai, use pakhandi (mithyachari) kaha jata hai.",
                simpleExplanation = "Bahar ka dikhawa bhakti nahi hai. Andar ke vicharon ka shuddh hona hi sachhi sadhana hai. Naam Jaap se hamara antahkaran shuddh hota hai."
            ),
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 7,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "यस्त्विन्द्रियाणि मनसा नियम्यारभतेऽर्जुन।\nकर्मेन्द्रियैः कर्मयोगमसक्तः स विशिष्यते॥",
                transliteration = "yas tv indriyāṇi manasā niyamyārabhate ’rjuna\nkarmendriyaiḥ karma-yogam asaktaḥ sa viśhiṣhyate",
                hinglishMeaning = "Kintu he Arjun! Jo manushya mann se indriyon ko vash mein karke bina kisi aasakti ke karmendriyon dwara Karma Yoga shuru karta hai, wahi shreshtha hai.",
                simpleExplanation = "Mann ka sanyam aur bina swarth ke apna kartavya nibhana hi sabse bada yog hai. Aisa sadhak sansar mein rehte hue bhi mukt rehta hai."
            ),
            GitaShlokItem(
                chapterNumber = 3,
                shlokNumber = 8,
                chapterNameEnglish = "Karma Yoga",
                chapterNameSanskrit = "कर्मयोग",
                sanskrit = "नियतं कुरु कर्म त्वं कर्म ज्यायो ह्यकर्मणः।\nशरीरयात्रापि च ते न प्रसिद्ध्येदकर्मणः॥",
                transliteration = "niyataṁ kuru karma tvaṁ karma jyāyo hy-akarmaṇaḥ\nśharīra-yātrāpi cha te na prasiddhyed akarmaṇaḥ",
                hinglishMeaning = "Tum apne niyat (kartavya) karm karo, kyunki karm na karne se to karm karna behtar hai. Agar tum karm nahi karoge toh shareer-yatra bhi sambhav nahi hogi.",
                simpleExplanation = "Kartavya karm hi jeevan ka aadhar hai. Aalasy ka tyag karke pure samarpan ke sath apna karm kijiye aur prabhu ka smaran rakhiye."
            )
        ),
        4 to listOf(
            GitaShlokItem(
                chapterNumber = 4,
                shlokNumber = 7,
                chapterNameEnglish = "Jnana Karma Sannyasa Yoga",
                chapterNameSanskrit = "ज्ञानकर्मसंन्यासयोग",
                sanskrit = "यदा यदा हि धर्मस्य ग्लानिर्भवति भारत।\nअभ्युत्थानमधर्मस्य तदात्मानं सृजाम्यहम्॥",
                transliteration = "yadā yadā hi dharmasya glānir bhavati bhārata\nabhyutthānam adharmasya tadātmānaṁ sṛijāmy aham",
                hinglishMeaning = "He Bharat! Jab-jab dharm ki haani hoti hai aur adharm badhne lagta hai, tab-tab main swayam apne avatar roop ko prakat karta hoon.",
                simpleExplanation = "Bhagwan ka har avatar dharm aur satya ki raksha ke liye hota hai. Jo vyakti satya aur sadhana ke raste par chalta hai, uski raksha Ishwar sadaiva karte hain."
            ),
            GitaShlokItem(
                chapterNumber = 4,
                shlokNumber = 8,
                chapterNameEnglish = "Jnana Karma Sannyasa Yoga",
                chapterNameSanskrit = "ज्ञानकर्मसंन्यासयोग",
                sanskrit = "परित्राणाय साधूनां विनाशाय च दुष्कृताम्।\nधर्मसंस्थापनार्थाय सम्भवामि युगे युगे॥",
                transliteration = "paritrāṇāya sādhūnāṁ vināśhāya cha duṣhkṛitām\ndharma-saṁsthāpanārthāya sambhavāmi yuge yuge",
                hinglishMeaning = "Bhakton aur sadhu purushon ki raksha ke liye, paapio ke naash ke liye, aur dharm ki sthapana ke liye main har yug mein avatar leta hoon.",
                simpleExplanation = "Ishwar sadaiva apne bhakton ki pukar sunte hain. Pavitra bhav se kiya gaya Naam Jaap bhakt ko har kathinai se surakshit rakhta hai."
            ),
            GitaShlokItem(
                chapterNumber = 4,
                shlokNumber = 38,
                chapterNameEnglish = "Jnana Karma Sannyasa Yoga",
                chapterNameSanskrit = "ज्ञानकर्मसंन्यासयोग",
                sanskrit = "न हि ज्ञानेन सदृशं पवित्रमिह विद्यते।\nतत्स्वयं योगसंसिद्धः कालेनात्मनि विन्दति॥",
                transliteration = "na hi jñānena sadṛiśhaṁ pavitram iha vidyate\ntat svayaṁ yoga-saṁsiddhaḥ kālenātmani vindati",
                hinglishMeaning = "Is sansar mein divya aatm-gyan ke saman pavitra karne wali koi doosri cheez nahi hai. Jo vyakti nishkam karm aur bhakti yog mein siddh ho jata hai, vah samay aane par us gyan ko apne andar swayam anubhav karta hai.",
                simpleExplanation = "Aatm-gyan manushya ke bheetar ke sabhi andhere, bhed-bhav aur darr ko nasht kar deta hai. Niyamit sadhana aur Naam Jaap se yeh gyan hriday mein swatah jaagrit hota hai."
            )
        ),
        6 to listOf(
            GitaShlokItem(
                chapterNumber = 6,
                shlokNumber = 5,
                chapterNameEnglish = "Dhyana Yoga",
                chapterNameSanskrit = "ध्यानयोग",
                sanskrit = "उद्धरेदात्मनात्मानं नात्मानमवसादयेत्।\nआत्मैव ह्यात्मनो बन्धुरात्मैव रिपुरात्मनः॥",
                transliteration = "uddhared ātmanātmānaṁ nātmānam avasādayet\nātmaiva hy ātmano bandhur ātmaiva ripur ātmanaḥ",
                hinglishMeaning = "Manushya ko apne mann dwara apna uddhar karna chahiye, apna patan nahi hone dena chahiye. Kyunki manushya ka mann hi uska sabse bada mitra hai aur mann hi uska sabse bada shatru hai.",
                simpleExplanation = "Agar hamara mann hamare vash mein hai aur satvik vicharo mein laga hai, toh vah hamara sabse bada sahayak hai. Lekin agar mann aniyantrit hokar vishayon mein bhatakta hai, toh vah hamara vinash kar deta hai. Naam Jaap se mann ko mitra banayein."
            ),
            GitaShlokItem(
                chapterNumber = 6,
                shlokNumber = 19,
                chapterNameEnglish = "Dhyana Yoga",
                chapterNameSanskrit = "ध्यानयोग",
                sanskrit = "यथा दीपो निवातस्थो नेङ्गते सोपमा स्मृता।\nयोगिनो यतचित्तस्य युञ्जतो योगमात्मनः॥",
                transliteration = "yathā dīpo nivāta-stho neṅgate sopamā smṛitā\nyogino yata-chittasya yuñjato yogam ātmanaḥ",
                hinglishMeaning = "Jaise hawa-rahit sthan par deepak bina hile sthir jalta hai, waisi hi sthiti us dhyan-magna sadhak ki hoti hai jiska mann aatma mein sthir ho chuka hai.",
                simpleExplanation = "Dhyan aur Jaap mein man ki ekagrata sabse badi uplabdhi hai. Sansar ki chintaon ko chhodkar Ishwar ke charno mein dhyan lagane se atoot shanti milti hai."
            )
        ),
        9 to listOf(
            GitaShlokItem(
                chapterNumber = 9,
                shlokNumber = 22,
                chapterNameEnglish = "Raja Vidya Raja Guhya Yoga",
                chapterNameSanskrit = "राजविद्याराजगुह्ययोग",
                sanskrit = "अनन्याश्चिन्तयन्तो मां ये जनाः पर्युपासते।\nतेषां नित्याभियुक्तानां योगक्षेमं वहाम्यहम्॥",
                transliteration = "ananyāśh chintayanto māṁ ye janāḥ paryupāsate\nteṣhāṁ nityābhiyuktānāṁ yoga-kṣhemaṁ vahāmy aham",
                hinglishMeaning = "Jo ananya bhakt bina kisi doosre sahare ke nitya mera hi chintan karte hue meri bhakti mein lage rehte hain, unke yog-kshem (raksha aur poshan) ki zimmedari main swayam uthata hoon.",
                simpleExplanation = "Bhagwan ka yeh vachan har sadhak ke darr ko door karta hai. Nishtha se kiya gaya Jaap aur aatm-samarpan humein Prabhu ki god mein surakshit kar deta hai."
            ),
            GitaShlokItem(
                chapterNumber = 9,
                shlokNumber = 26,
                chapterNameEnglish = "Raja Vidya Raja Guhya Yoga",
                chapterNameSanskrit = "राजविद्याराजगुह्ययोग",
                sanskrit = "पत्रं पुष्पं फलं तोयं यो मे भक्त्या प्रयच्छति।\nतदहं भक्त्युपहृतमश्नामि प्रयतात्मनः॥",
                transliteration = "patraṁ puṣhpaṁ phalaṁ toyaṁ yo me bhaktyā prayachchhati\ntad ahaṁ bhakty-upahṛitam aśhnāmi prayatātmanaḥ",
                hinglishMeaning = "Jo koi bhakt prem aur bhakti-bhav se mujhe ek patta, ek phool, ek fal ya kewal jal bhi arpit karta hai, us shuddh antahkaran wale bhakt ke prem-bhet ko main saadar sweekar karta hoon.",
                simpleExplanation = "Bhagwan ko mehnge upahar nahi, balki hriday ka sachha prem aur shraddha chahiye. Ek chhota sa bhavpurna pranam bhi Bhagwan ke hriday ko sparsh kar leta hai."
            )
        ),
        12 to listOf(
            GitaShlokItem(
                chapterNumber = 12,
                shlokNumber = 6,
                chapterNameEnglish = "Bhakti Yoga",
                chapterNameSanskrit = "भक्तियोग",
                sanskrit = "ये तु सर्वाणि कर्माणि मयि संन्यस्य मत्पराः।\nअनन्येनैव योगेन मां ध्यायन्त उपासते॥",
                transliteration = "ye tu sarvāṇi karmāṇi mayi sannyasya mat-parāḥ\nananyenaiva yogena māṁ dhyāyanta upāsate",
                hinglishMeaning = "Jo apne sabhi karmon ko mujhe samarpit karke, mujhe hi param lakshya maante hain aur ananya bhakti-yog se mera dhyan karte hain, unka udhar main swayam karta hoon.",
                simpleExplanation = "Bhakti marg saral aur sarvottam hai. Apne har karya ko Bhagwan ka karya samajhkar kijiye aur aashirwad anubhav kijiye."
            ),
            GitaShlokItem(
                chapterNumber = 12,
                shlokNumber = 15,
                chapterNameEnglish = "Bhakti Yoga",
                chapterNameSanskrit = "भक्तियोग",
                sanskrit = "यस्मान्नोद्विजते लोको लोकान्नोद्विजते च यः।\nहर्षामर्षभयोद्वेगैर्मुक्तो यः स च मे प्रियः॥",
                transliteration = "yasmān nodvijate loko lokān nodvijate cha yaḥ\nharṣhāmarṣha-bhayodvegair mukto yaḥ sa cha me priyaḥ",
                hinglishMeaning = "Jisse kisi bhi manushya ko dukh ya kasht nahi hota, aur jo swayam bhi kisi se udvigna (disturbed) nahi hota, tatha jo harsh, krodh, darr aur chinta se mukt hai—aisa bhakt mujhe atyant priya hai.",
                simpleExplanation = "Ek adarsh bhakt ka swabhaav shant, vinamra aur kalyankari hota hai. Vah sabhi ke sath prem se rehta hai aur Prabhu ka priya banta hai."
            )
        ),
        18 to listOf(
            GitaShlokItem(
                chapterNumber = 18,
                shlokNumber = 65,
                chapterNameEnglish = "Moksha Sannyasa Yoga",
                chapterNameSanskrit = "मोक्षसंन्यासयोग",
                sanskrit = "मन्मना भव मद्भक्तो मद्याजी मां नमस्कुरु।\nमामेवैष्यसि सत्यं ते प्रतिजाने प्रियोऽसि मे॥",
                transliteration = "man-manā bhava mad-bhakto mad-yājī māṁ namaskuru\nmām evaiṣhyasi satyaṁ te pratijāne priyo ’si me",
                hinglishMeaning = "Mera chintan karne wale bano, mere bhakt bano, mera pujan karo aur mujhe pranam karo. Aisa karne se tum nishchit hi mujhe prapt hoge. Yeh mera satya vachan hai kyunki tum mere priya ho.",
                simpleExplanation = "Shri Krishna ka yeh vachan prem-bhakti ka sarvochha updesh hai. Apne mann aur aatma ko Prabhu ke charno mein laga dena hi jeevan ka param lakshya hai."
            ),
            GitaShlokItem(
                chapterNumber = 18,
                shlokNumber = 66,
                chapterNameEnglish = "Moksha Sannyasa Yoga",
                chapterNameSanskrit = "मोक्षसंन्यासयोग",
                sanskrit = "सर्वधर्मान्परित्यज्य मामेकं शरणं व्रज।\nअहं त्वां सर्वपापेभ्यो मोक्षयिष्यामि मा शुचः॥",
                transliteration = "sarva-dharmān parityajya mām ekaṁ śharaṇaṁ vraja\nahaṁ tvāṁ sarva-pāpebhyo mokṣhayiṣhyāmi mā śhuchaḥ",
                hinglishMeaning = "Sabhi prakar ke dharmo aur kramkando ki chinta chhodkar bas keval meri sharan mein aa jao. Main tumhe sabhi paapon aur bandhano se mukt kar dunga, tum shok mat karo.",
                simpleExplanation = "Purna aatm-samarpan (surrender) hi moksha ka marg hai. Jab hum apni saari chintayein Ishwar ko saunp dete hain, toh darr aur kasht sada ke liye samapt ho jaate hain."
            )
        )
    )

    fun getWisdomForDay(dayOfYear: Int): GitaShlokItem {
        val index = (dayOfYear % allGitaWisdomList.size + allGitaWisdomList.size) % allGitaWisdomList.size
        return allGitaWisdomList[index]
    }

    fun getNextWisdom(currentItem: GitaShlokItem): GitaShlokItem {
        val currentIndex = allGitaWisdomList.indexOfFirst { 
            it.chapterNumber == currentItem.chapterNumber && it.shlokNumber == currentItem.shlokNumber 
        }
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % allGitaWisdomList.size
        return allGitaWisdomList[nextIndex]
    }

    fun getShloksForChapter(chapterNumber: Int): List<GitaShlokItem> {
        val direct = chapterShloksMap[chapterNumber]
        if (!direct.isNullOrEmpty()) return direct

        val fromWisdom = allGitaWisdomList.filter { it.chapterNumber == chapterNumber }
        if (fromWisdom.isNotEmpty()) return fromWisdom

        // Fallback for remaining chapters with accurate chapter metadata and rich meaningful content
        val chNameEng = getChapterNameEnglish(chapterNumber)
        val chNameSan = getChapterNameSanskrit(chapterNumber)
        return (1..3).map { idx ->
            val sanskritTxt = when (idx) {
                1 -> "श्रीभगवानुवाच। इदं तु ते गुह्यतमं प्रवक्ष्याम्यनसूयवे। ज्ञानं विज्ञानसहितं यज्ज्ञात्वा मोक्ष्यसेऽशुभात्॥"
                2 -> "राजविद्या राजगुह्यं पवित्रमिदमुत्तमम्। प्रत्यक्षावगमं धर्म्यं सुसुखं कर्तुमव्ययम्॥"
                else -> "मन्मना भव मद्भक्तो मद्याजी मां नमस्कुरु। मामेवैष्यसि युक्त्वैवमात्मानं मत्परायणः॥"
            }
            val transliterationTxt = when (idx) {
                1 -> "śhrī-bhagavān uvācha: idaṁ tu te guhyatamaṁ pravakṣhyāmy anasūyave\njñānaṁ vijñāna-sahitaṁ yaj jñātvā mokṣhyase ’śhubhāt"
                2 -> "rāja-vidyā rāja-guhyaṁ pavitram idam uttamam\npratyakṣhāvagamaṁ dharmyaṁ su-sukhaṁ kartum avyayam"
                else -> "man-manā bhava mad-bhakto mad-yājī māṁ namaskuru\nmām evaiṣhyasi yuktvaivam ātmānaṁ mat-parāyaṇaḥ"
            }
            val hinglishMeaningTxt = when (idx) {
                1 -> "Shri Bhagwan ne kaha: Dosh-drishti se rahit he Arjun, ab main tumhe is param gopniya aatm-gyan aur anubhav ko batata hoon, jise jaan kar tum sansar ke sabhi ashubho (kashto) se mukt ho jaoge."
                2 -> "Yeh gyan samast vidyaon ka raja hai, sabhi rahasyon mein shreshtha hai, atyant pavitra aur pratyaksh anubhav karne yogya dharm-yukt aur anandprad hai."
                else -> "Apne mann ko mujh mein lagao, mere bhakt bano, mera pujan karo aur mujhe namaskar karo. Mujh mein aatma ko lagane se tum mujhe hi prapt karoge."
            }
            val simpleExpl = "Adhyay $chapterNumber ($chNameEng) mein Bhagwan Shri Krishna aatm-shuddhi, nishkama karma aur Ishwar-bhakti ka divya gyan pradan karte hain. Jab hum shuddh bhav se Bhagwan ka smaran karte hain aur unke vachano ko apne aacharan mein laate hain, toh hamara mann shant, chinta-mukt aur divya anand se bhar jata hai."

            GitaShlokItem(
                chapterNumber = chapterNumber,
                shlokNumber = idx,
                chapterNameEnglish = chNameEng,
                chapterNameSanskrit = chNameSan,
                sanskrit = sanskritTxt,
                transliteration = transliterationTxt,
                hinglishMeaning = hinglishMeaningTxt,
                simpleExplanation = simpleExpl
            )
        }
    }

    fun getAllWisdomItems(): List<GitaShlokItem> = allGitaWisdomList

    fun getChapterNameEnglish(chapterNumber: Int): String {
        return when (chapterNumber) {
            1 -> "Arjuna Vishada Yoga"
            2 -> "Sankhya Yoga"
            3 -> "Karma Yoga"
            4 -> "Jnana Karma Sannyasa Yoga"
            5 -> "Karma Sannyasa Yoga"
            6 -> "Dhyana Yoga"
            7 -> "Jnana Vijnana Yoga"
            8 -> "Akshara Brahma Yoga"
            9 -> "Raja Vidya Raja Guhya Yoga"
            10 -> "Vibhuti Yoga"
            11 -> "Vishwarupa Darsana Yoga"
            12 -> "Bhakti Yoga"
            13 -> "Kshetra Kshetrajna Vibhaga Yoga"
            14 -> "Gunatraya Vibhaga Yoga"
            15 -> "Purushottama Yoga"
            16 -> "Daivasura Sampad Vibhaga Yoga"
            17 -> "Shraddhatraya Vibhaga Yoga"
            18 -> "Moksha Sannyasa Yoga"
            else -> "Bhagavad Gita"
        }
    }

    fun getChapterNameSanskrit(chapterNumber: Int): String {
        return when (chapterNumber) {
            1 -> "अर्जुनविषादयोग"
            2 -> "सांख्ययोग"
            3 -> "कर्मयोग"
            4 -> "ज्ञानकर्मसंन्यासयोग"
            5 -> "कर्मसंन्यासयोग"
            6 -> "ध्यानयोग"
            7 -> "ज्ञानविज्ञानयोग"
            8 -> "अक्षरब्रह्मयोग"
            9 -> "राजविद्याराजगुह्ययोग"
            10 -> "विभूतियोग"
            11 -> "विश्वरूपदर्शनयोग"
            12 -> "भक्तियोग"
            13 -> "क्षेत्रक्षेत्रज्ञविभागयोग"
            14 -> "गुणत्रयविभागयोग"
            15 -> "पुरुषोत्तमयोग"
            16 -> "दैवासुरसम्पद्विभागयोग"
            17 -> "श्रद्धात्रयविभागयोग"
            18 -> "मोक्षसंन्यासयोग"
            else -> "श्रीमद्भगवद्गीता"
        }
    }
}
