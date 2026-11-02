package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Bg = Color(0xFF080B12)
private val Panel = Color(0xFF151B27)
private val Gold = Color(0xFFFFC857)
private val Muted = Color(0xFF9BA7B8)

data class Film(
    val id:String,
    val title:String,
    val year:Int,
    val genre:String,
    val rating:String,
    val summary:String,
    val url:String,
    val premium:Boolean=false
)

private val sampleFilms = listOf(
    Film("sample-1","Koinot sarguzashti",2025,"Fantastika • Sarguzasht","8.8","Sirli galaktikada insoniyat taqdirini o‘zgartiradigan safar boshlanadi.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
    Film("sample-2","So‘nggi qahramon",2024,"Jangari • Drama","8.2","O‘z shahrini himoya qilish uchun qaytgan qahramon haqida hikoya.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"),
    Film("sample-3","Sehrli olam",2023,"Oilaviy • Fantastika","7.9","Do‘stlik va jasorat sinovdan o‘tadigan sehrli olam.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
    Film("sample-4","Maxfiy topshiriq",2025,"Triller • Jangari","8.5","Murakkab topshiriq ortida yashiringan haqiqatni izlash.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"),
    Film("sample-5","Yangi hayot",2022,"Drama • Romantika","7.6","Kutilmagan uchrashuv ikki inson hayotini o‘zgartiradi.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
    Film("sample-6","Kelajak shahri",2026,"Fantastika","9.0","Texnologiyalar hukmron kelajakda insoniylik uchun kurash.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs=getSharedPreferences("alfaplay",MODE_PRIVATE)
        setContent {
            val api= remember { SupabaseApi(BuildConfig.SUPABASE_URL,BuildConfig.SUPABASE_ANON_KEY) }
            val scope= rememberCoroutineScope()
            var tab by remember { mutableStateOf("Asosiy") }
            var selected by remember { mutableStateOf<Film?>(null) }
            var query by remember { mutableStateOf("") }
            var accessToken by remember { mutableStateOf(prefs.getString("access_token",null)) }
            var refreshToken by remember { mutableStateOf(prefs.getString("refresh_token",null)) }
            var userId by remember { mutableStateOf(prefs.getString("user_id",null)) }
            var userEmail by remember { mutableStateOf(prefs.getString("user_email",null)) }
            var role by remember { mutableStateOf("user") }
            var serverFilms by remember { mutableStateOf<List<Film>>(emptyList()) }
            var saved by remember { mutableStateOf(prefs.getStringSet("saved",emptySet())?.toSet() ?: emptySet()) }
            var busy by remember { mutableStateOf(false) }
            var status by remember { mutableStateOf(if(api.isConfigured) "Server bilan bog‘lanishga tayyor." else "Server sozlanmagan — hozircha mahalliy katalog.") }

            fun persistAuth(auth:AuthResult) {
                accessToken=auth.accessToken
                refreshToken=auth.refreshToken
                userId=auth.userId
                userEmail=auth.email
                prefs.edit().putString("access_token",auth.accessToken)
                    .putString("refresh_token",auth.refreshToken)
                    .putString("user_id",auth.userId)
                    .putString("user_email",auth.email).apply()
            }

            LaunchedEffect(api.isConfigured,accessToken,userId) {
                if(api.isConfigured) {
                    try {
                        val result=withContext(Dispatchers.IO) {
                            val movies=api.getMovies(accessToken)
                            val favs=if(!accessToken.isNullOrBlank() && !userId.isNullOrBlank()) api.getFavoriteIds(accessToken!!,userId!!) else emptySet()
                            val currentRole=if(!accessToken.isNullOrBlank() && !userId.isNullOrBlank()) api.getProfileRole(accessToken!!,userId!!) else "user"
                            Triple(movies,favs,currentRole)
                        }
                        serverFilms=result.first.map { Film(it.id,it.title,it.year,it.genre,it.rating,it.summary,it.videoUrl,it.isPremium) }
                        if(accessToken!=null) saved=result.second
                        role=result.third
                        status="Server ulandi • ${serverFilms.size} ta kino topildi"
                    } catch(e:Exception) {
                        status="Server xabari: ${e.message ?: "ulanishda xatolik"}"
                    }
                }
            }

            MaterialTheme(colorScheme=darkColorScheme(primary=Gold,background=Bg,surface=Panel)) {
                Surface(Modifier.fillMaxSize(),color=Bg) {
                    Column {
                        Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=13.dp),verticalAlignment=Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("ALFA",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold,letterSpacing=4.sp)
                                Text("PLAY",color=Color.White,fontSize=27.sp,fontWeight=FontWeight.Black,letterSpacing=3.sp)
                            }
                            Surface(color=Panel,shape=RoundedCornerShape(50)) {
                                Text(if(role=="admin") "ADMIN ✦" else if(accessToken!=null) "AKTIV ✦" else "GUEST",color=Gold,modifier=Modifier.padding(horizontal=13.dp,vertical=8.dp),fontWeight=FontWeight.Bold,fontSize=11.sp)
                            }
                        }

                        if(selected!=null) {
                            FilmDetail(selected!!,saved.contains(selected!!.id),{
                                val film=selected!!
                                val next=if(saved.contains(film.id)) saved-film.id else saved+film.id
                                saved=next
                                prefs.edit().putStringSet("saved",next).apply()
                                if(accessToken!=null && userId!=null && !film.id.startsWith("sample-")) {
                                    scope.launch {
                                        try { withContext(Dispatchers.IO) { if(next.contains(film.id)) api.addFavorite(accessToken!!,userId!!,film.id) else api.removeFavorite(accessToken!!,userId!!,film.id) } }
                                        catch(e:Exception) { status="Sevimlilar serverga saqlanmadi: ${e.message}" }
                                    }
                                }
                            },{ film -> if(film.url.isNotBlank()) startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(film.url))) else status="Bu kino uchun video manbasi kiritilmagan." },{selected=null})
                        } else {
                            val catalog=if(api.isConfigured) serverFilms else sampleFilms
                            when(tab) {
                                "Qidiruv" -> {
                                    OutlinedTextField(query,{query=it},Modifier.fillMaxWidth().padding(horizontal=16.dp),label={Text("Kino nomi yoki janr")},singleLine=true)
                                    FilmList(catalog.filter { it.title.contains(query,true) || it.genre.contains(query,true) },saved,{selected=it},{film->
                                        val next=if(saved.contains(film.id)) saved-film.id else saved+film.id
                                        saved=next; prefs.edit().putStringSet("saved",next).apply()
                                        if(accessToken!=null && userId!=null && !film.id.startsWith("sample-")) scope.launch { runCatching { withContext(Dispatchers.IO) { if(next.contains(film.id)) api.addFavorite(accessToken!!,userId!!,film.id) else api.removeFavorite(accessToken!!,userId!!,film.id) } }.onFailure { status="Sevimlilar sinxronlanmadi: ${it.message}" } }
                                    })
                                }
                                "Saqlangan" -> FilmList(catalog.filter{saved.contains(it.id)},saved,{selected=it},{film->
                                    val next=saved-film.id; saved=next; prefs.edit().putStringSet("saved",next).apply()
                                    if(accessToken!=null && userId!=null && !film.id.startsWith("sample-")) scope.launch { runCatching { withContext(Dispatchers.IO) { api.removeFavorite(accessToken!!,userId!!,film.id) } }.onFailure { status="O‘chirish amalga oshmadi: ${it.message}" } }
                                })
                                "Profil" -> ProfilePage(
                                    email=userEmail,configured=api.isConfigured,status=status,busy=busy,
                                    onAuth={email,password,name,signup->
                                        scope.launch {
                                            busy=true
                                            try {
                                                val auth=withContext(Dispatchers.IO) { if(signup) api.signUp(email,password,name) else api.signIn(email,password) }
                                                if(auth.accessToken.isBlank()) status="Ro‘yxatdan o‘tish so‘rovi yuborildi. Emailingizni tasdiqlang, so‘ng tizimga kiring."
                                                else { persistAuth(auth); status="Kirish muvaffaqiyatli." }
                                            } catch(e:Exception) { status=e.message ?: "Kirishda xatolik." }
                                            busy=false
                                        }
                                    },
                                    onLogout={
                                        prefs.edit().remove("access_token").remove("refresh_token").remove("user_id").remove("user_email").apply()
                                        accessToken=null; refreshToken=null; userId=null; userEmail=null; role="user"; saved=emptySet()
                                        status="Hisobdan chiqildi."
                                    }
                                )
                                "Obuna" -> PlansPage(configured=api.isConfigured,loggedIn=accessToken!=null)
                                "Admin" -> if(role=="admin" && accessToken!=null) AdminPage(
                                    busy=busy,
                                    onAdd={title,description,year,url->
                                        scope.launch {
                                            busy=true
                                            try {
                                                withContext(Dispatchers.IO) { api.createMovie(accessToken!!,title,description,year,url) }
                                                val refreshed=withContext(Dispatchers.IO) { api.getMovies(accessToken) }
                                                serverFilms=refreshed.map { Film(it.id,it.title,it.year,it.genre,it.rating,it.summary,it.videoUrl,it.isPremium) }
                                                status="Kino server katalogiga qo‘shildi."
                                            } catch(e:Exception) { status="Kino qo‘shilmadi: ${e.message}" }
                                            busy=false
                                        }
                                    },
                                    onDelete={id->
                                        scope.launch {
                                            busy=true
                                            try {
                                                withContext(Dispatchers.IO) { api.deleteMovie(accessToken!!,id) }
                                                serverFilms=serverFilms.filterNot{it.id==id}
                                                status="Kino katalogdan o‘chirildi."
                                            } catch(e:Exception) { status="O‘chirishda xatolik: ${e.message}" }
                                            busy=false
                                        }
                                    },
                                    movies=serverFilms
                                ) else ProfilePage(email=userEmail,configured=api.isConfigured,status="Admin huquqi yo‘q.",busy=false,onAuth={_,_,_,_->},onLogout={})
                                else -> {
                                    Column(Modifier.padding(horizontal=18.dp)) {
                                        Text("KINOLAR OLAMIGA XUSH KELIBSIZ",color=Muted,fontSize=11.sp,letterSpacing=1.3.sp)
                                        Text("Sevimli filmlaringizni\nshu yerda tomosha qiling",color=Color.White,fontSize=23.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=7.dp,bottom=14.dp))
                                        Surface(color=Color(0xFF342A19),shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth()) {
                                            Column(Modifier.padding(17.dp)) {
                                                Text("ALFAPLAY PREMIERE",color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp)
                                                Text(if(api.isConfigured) "Server katalogi" else "Kino olamiga xush kelibsiz",color=Color.White,fontSize=19.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=7.dp))
                                                Text(status,color=Color(0xFFE2D9C8),fontSize=12.sp,modifier=Modifier.padding(top=6.dp))
                                            }
                                        }
                                        Text(if(api.isConfigured) "SERVERDAGI KINOLAR" else "ILOVA NAMUNA KATALOGI",color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp,modifier=Modifier.padding(top=18.dp,bottom=8.dp))
                                    }
                                    FilmList(catalog,saved,{selected=it},{film->
                                        val next=if(saved.contains(film.id)) saved-film.id else saved+film.id
                                        saved=next; prefs.edit().putStringSet("saved",next).apply()
                                        if(accessToken!=null && userId!=null && !film.id.startsWith("sample-")) scope.launch { runCatching { withContext(Dispatchers.IO) { if(next.contains(film.id)) api.addFavorite(accessToken!!,userId!!,film.id) else api.removeFavorite(accessToken!!,userId!!,film.id) } }.onFailure { status="Sevimlilar sinxronlanmadi: ${it.message}" } }
                                    })
                                }
                            }
                        }

                        if(selected==null) NavigationBar(containerColor=Panel) {
                            val tabs=buildList { addAll(listOf("Asosiy","Qidiruv","Saqlangan","Obuna","Profil")); if(role=="admin") add("Admin") }
                            tabs.forEach { item ->
                                val icon=when(item) {"Asosiy"->Icons.Default.Home;"Qidiruv"->Icons.Default.Search;"Saqlangan"->Icons.Default.Bookmark;"Obuna"->Icons.Default.Star;"Admin"->Icons.Default.AdminPanelSettings;else->Icons.Default.Person}
                                NavigationBarItem(selected=tab==item,onClick={tab=item;query=""},icon={Icon(icon,contentDescription=item)},label={Text(item,fontSize=9.sp)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Gold,selectedTextColor=Gold,indicatorColor=Color(0xFF39301F),unselectedIconColor=Muted,unselectedTextColor=Muted))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilmList(items:List<Film>,saved:Set<String>,onOpen:(Film)->Unit,onSave:(Film)->Unit) {
    if(items.isEmpty()) Box(Modifier.fillMaxWidth().padding(36.dp),contentAlignment=Alignment.Center) { Text("Hozircha server katalogida kino yo‘q.",color=Muted) }
    LazyColumn(contentPadding=PaddingValues(start=16.dp,end=16.dp,bottom=12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        items(items) { film ->
            Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().clickable{onOpen(film)}) {
                Row(Modifier.padding(13.dp),verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp,94.dp).background(Color(0xFF293447),RoundedCornerShape(10.dp)),contentAlignment=Alignment.Center) { Icon(Icons.Default.Movie,null,tint=Gold,modifier=Modifier.size(32.dp)) }
                    Column(Modifier.weight(1f).padding(start=12.dp)) {
                        Text(film.title,color=Color.White,fontWeight=FontWeight.Bold,fontSize=15.sp)
                        Text((if(film.year>0) film.year.toString() else "Yil ko‘rsatilmagan")+" • "+film.genre,color=Muted,fontSize=10.sp,modifier=Modifier.padding(top=5.dp))
                        Text("★ "+film.rating+(if(film.premium) "  •  VIP" else ""),color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp,modifier=Modifier.padding(top=7.dp))
                        Text("Batafsil ko‘rish  ›",color=Gold,fontSize=11.sp,modifier=Modifier.padding(top=6.dp))
                    }
                    IconButton(onClick={onSave(film)}) { Icon(if(saved.contains(film.id)) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,null,tint=Gold) }
                }
            }
        }
    }
}

@Composable
private fun FilmDetail(film:Film,isSaved:Boolean,onSave:()->Unit,onPlay:(Film)->Unit,onBack:()->Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("‹  ORQAGA",color=Gold,fontWeight=FontWeight.Bold,modifier=Modifier.clickable{onBack()}.padding(vertical=8.dp))
        Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFF202C3D),RoundedCornerShape(20.dp)),contentAlignment=Alignment.Center) { Icon(Icons.Default.Movie,null,tint=Gold,modifier=Modifier.size(60.dp)) }
        Text(film.title,color=Color.White,fontSize=26.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=17.dp))
        Text((if(film.year>0) film.year.toString() else "")+" • "+film.genre+" • ★ "+film.rating,color=Gold,fontSize=12.sp,modifier=Modifier.padding(top=7.dp))
        Text(film.summary,color=Color(0xFFD0D6E0),fontSize=14.sp,lineHeight=22.sp,modifier=Modifier.padding(top=16.dp))
        Button(onClick={onPlay(film)},Modifier.fillMaxWidth().padding(top=22.dp),colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Color.Black)) { Icon(Icons.Default.PlayArrow,null); Text("  TOMOSHA QILISH",fontWeight=FontWeight.Bold) }
        OutlinedButton(onClick=onSave,Modifier.fillMaxWidth().padding(top=8.dp)) { Icon(if(isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,null); Text(if(isSaved) "  Saqlanganlardan olish" else "  Saqlanganlarga qo‘shish") }
    }
}

@Composable
private fun ProfilePage(
    email:String?,
    configured:Boolean,
    status:String,
    busy:Boolean,
    onAuth:(String,String,String,Boolean)->Unit,
    onLogout:()->Unit
) {
    var name by remember { mutableStateOf("") }
    var mail by remember { mutableStateOf(email ?: "") }
    var password by remember { mutableStateOf("") }
    var signup by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("SHAXSIY KABINET",color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp)
        Text(if(email!=null) "Xush kelibsiz!" else "Hisobingizga kiring",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp))
        Text(status,color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=8.dp,bottom=14.dp))
        if(!configured) {
            Text("Server hali sozlanmagan. Supabase URL va anon kaliti kiritilgach, ro‘yxatdan o‘tish va server sinxronizatsiyasi ishlaydi.",color=Color.White,fontSize=14.sp)
        } else if(email!=null) {
            Text(email,color=Color.White,fontSize=16.sp,modifier=Modifier.padding(vertical=12.dp))
            Text("Hisob server bilan bog‘langan.",color=Muted)
            Button(onClick=onLogout,modifier=Modifier.fillMaxWidth().padding(top=18.dp),colors=ButtonDefaults.buttonColors(containerColor=Panel)) { Text("Hisobdan chiqish",color=Color.White) }
        } else {
            if(signup) OutlinedTextField(name,{name=it},Modifier.fillMaxWidth(),label={Text("Ismingiz")},singleLine=true)
            OutlinedTextField(mail,{mail=it},Modifier.fillMaxWidth().padding(top=8.dp),label={Text("Email")},singleLine=true)
            OutlinedTextField(password,{password=it},Modifier.fillMaxWidth().padding(top=8.dp),label={Text("Parol (kamida 6 belgi)")},singleLine=true,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password))
            Button(onClick={onAuth(mail.trim(),password,name.trim(),signup)},enabled=!busy && mail.contains("@") && password.length>=6 && (!signup || name.isNotBlank()),modifier=Modifier.fillMaxWidth().padding(top=14.dp),colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Color.Black)) {
                if(busy) CircularProgressIndicator(Modifier.size(18.dp),strokeWidth=2.dp) else Text(if(signup) "RO‘YXATDAN O‘TISH" else "KIRISH",fontWeight=FontWeight.Bold)
            }
            TextButton(onClick={signup=!signup}) { Text(if(signup) "Hisobingiz bormi? Kirish" else "Yangi hisob yaratish",color=Gold) }
        }
    }
}

@Composable
private fun PlansPage(configured:Boolean,loggedIn:Boolean) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("ALFAPLAY VIP",color=Gold,fontSize=13.sp,fontWeight=FontWeight.Bold)
        Text("Premium obuna",color=Color.White,fontSize=25.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp,bottom=12.dp))
        Text("Premium narxlari va to‘lov tizimi server egasi tomonidan tasdiqlanmaguncha xarid faollashtirilmaydi.",color=Muted,fontSize=13.sp)
        Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().padding(top=16.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("PREMIUM",color=Gold,fontWeight=FontWeight.Bold)
                Text("Reklamasiz tomosha • Yuqori sifat • Maxsus kontent",color=Color.White,modifier=Modifier.padding(top=8.dp))
                Text(if(!configured) "Server sozlanmagan" else if(!loggedIn) "Obuna uchun avval hisobga kiring" else "To‘lov provayderi hali ulanmagan",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=12.dp))
            }
        }
    }
}

@Composable
private fun AdminPage(busy:Boolean,onAdd:(String,String,Int,String)->Unit,onDelete:(String)->Unit,movies:List<Film>) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2026") }
    var videoUrl by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("ADMIN BOSHQARUVI",color=Gold,fontWeight=FontWeight.Bold,fontSize=13.sp)
        Text("Kino qo‘shish",color=Color.White,fontSize=22.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp))
        OutlinedTextField(title,{title=it},Modifier.fillMaxWidth(),label={Text("Kino nomi")},singleLine=true)
        OutlinedTextField(description,{description=it},Modifier.fillMaxWidth().padding(top=6.dp),label={Text("Tavsif")},minLines=2)
        OutlinedTextField(year,{year=it},Modifier.fillMaxWidth().padding(top=6.dp),label={Text("Yili")},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))
        OutlinedTextField(videoUrl,{videoUrl=it},Modifier.fillMaxWidth().padding(top=6.dp),label={Text("Video URL")},singleLine=true)
        Button(onClick={onAdd(title.trim(),description.trim(),year.toIntOrNull() ?: 2026,videoUrl.trim());title="";description="";videoUrl=""},enabled=!busy && title.isNotBlank() && videoUrl.startsWith("https://"),modifier=Modifier.fillMaxWidth().padding(top=10.dp),colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Color.Black)) { Text(if(busy) "KUTILMOQDA..." else "SERVERGA KINO QO‘SHISH") }
        Text("Katalogdagi kinolar",color=Gold,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=18.dp,bottom=6.dp))
        LazyColumn(verticalArrangement=Arrangement.spacedBy(6.dp)) {
            items(movies) { film ->
                Row(Modifier.fillMaxWidth().background(Panel,RoundedCornerShape(10.dp)).padding(10.dp),verticalAlignment=Alignment.CenterVertically) {
                    Text(film.title,color=Color.White,modifier=Modifier.weight(1f))
                    IconButton(onClick={onDelete(film.id)}) { Icon(Icons.Default.Delete,null,tint=Color(0xFFFF7777)) }
                }
            }
        }
    }
}
