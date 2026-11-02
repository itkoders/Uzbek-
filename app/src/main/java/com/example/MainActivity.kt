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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF080B12)
private val Panel = Color(0xFF151B27)
private val Gold = Color(0xFFFFC857)
private val Muted = Color(0xFF9BA7B8)

data class Film(val id:Int,val title:String,val year:Int,val genre:String,val rating:String,val summary:String,val url:String)
private val films = listOf(
 Film(1,"Koinot sarguzashti",2025,"Fantastika • Sarguzasht","8.8","Sirli galaktikada insoniyat taqdirini o‘zgartiradigan safar boshlanadi.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
 Film(2,"So‘nggi qahramon",2024,"Jangari • Drama","8.2","O‘z shahrini himoya qilish uchun qaytgan qahramon haqida hikoya.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"),
 Film(3,"Sehrli olam",2023,"Oilaviy • Fantastika","7.9","Do‘stlik va jasorat sinovdan o‘tadigan sehrli olam.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
 Film(4,"Maxfiy topshiriq",2025,"Triller • Jangari","8.5","Murakkab topshiriq ortida yashiringan haqiqatni izlash.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"),
 Film(5,"Yangi hayot",2022,"Drama • Romantika","7.6","Kutilmagan uchrashuv ikki inson hayotini o‘zgartiradi.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
 Film(6,"Kelajak shahri",2026,"Fantastika","9.0","Texnologiyalar hukmron kelajakda insoniylik uchun kurash.","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
)

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val prefs=getSharedPreferences("alfaplay", MODE_PRIVATE)
  setContent {
   var tab by remember { mutableStateOf("Asosiy") }
   var selected by remember { mutableStateOf<Film?>(null) }
   var query by remember { mutableStateOf("") }
   var saved by remember { mutableStateOf(prefs.getStringSet("saved", emptySet())?.toSet() ?: emptySet()) }
   val visible=films.filter { it.title.contains(query,true) || it.genre.contains(query,true) }
   MaterialTheme(colorScheme=darkColorScheme(primary=Gold,background=Bg,surface=Panel)) {
    Surface(Modifier.fillMaxSize(),color=Bg) {
     Column {
      Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=16.dp),verticalAlignment=Alignment.CenterVertically) {
       Column(Modifier.weight(1f)) {
        Text("ALFA",color=Gold,fontSize=13.sp,fontWeight=FontWeight.Bold,letterSpacing=4.sp)
        Text("PLAY",color=Color.White,fontSize=28.sp,fontWeight=FontWeight.Black,letterSpacing=3.sp)
       }
       Surface(color=Panel,shape=RoundedCornerShape(50)) { Text("VIP  ✦",color=Gold,modifier=Modifier.padding(horizontal=14.dp,vertical=9.dp),fontWeight=FontWeight.Bold) }
      }
      if(selected!=null) {
       FilmDetail(selected!!,saved.contains(selected!!.id.toString()),{ film ->
        val id=film.id.toString()
        saved=if(saved.contains(id)) saved-id else saved+id
        prefs.edit().putStringSet("saved",saved).apply()
       },{ film -> startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(film.url))) },{selected=null})
      } else {
       when(tab) {
        "Qidiruv" -> {
         OutlinedTextField(query,{query=it},Modifier.fillMaxWidth().padding(horizontal=16.dp),label={Text("Kino nomi yoki janr")},singleLine=true)
         FilmList(visible,saved,{selected=it},{ film ->
          val id=film.id.toString(); saved=if(saved.contains(id)) saved-id else saved+id
          prefs.edit().putStringSet("saved",saved).apply()
         })
        }
        "Saqlangan" -> FilmList(films.filter{saved.contains(it.id.toString())},saved,{selected=it},{ film ->
         val id=film.id.toString(); saved=saved-id; prefs.edit().putStringSet("saved",saved).apply()
        })
        "Profil" -> ProfilePage()
        "Obuna" -> PlansPage()
        else -> {
         Column(Modifier.padding(horizontal=18.dp)) {
          Text("KINOLAR OLAMIGA XUSH KELIBSIZ",color=Muted,fontSize=11.sp,letterSpacing=1.5.sp)
          Text("Sevimli filmlaringizni\nshu yerda tomosha qiling",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp,bottom=16.dp))
          Surface(color=Color(0xFF342A19),shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth()) {
           Column(Modifier.padding(18.dp)) {
            Text("ALFAPLAY PREMIERE",color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp)
            Text("Har kuni yangi taassurot",color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=7.dp))
            Text("Katalogni ko‘ring va o‘zingizga yoqqan kinoni tanlang.",color=Color(0xFFE2D9C8),fontSize=13.sp,modifier=Modifier.padding(top=6.dp))
           }
          }
          Text("TAVSIYA ETILADI",color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp,modifier=Modifier.padding(top=20.dp,bottom=8.dp))
         }
         FilmList(films,saved,{selected=it},{ film ->
          val id=film.id.toString(); saved=if(saved.contains(id)) saved-id else saved+id
          prefs.edit().putStringSet("saved",saved).apply()
         })
        }
       }
      }
      if(selected==null) NavigationBar(containerColor=Panel) {
       listOf("Asosiy","Qidiruv","Saqlangan","Obuna","Profil").forEach { item ->
        val icon=when(item){"Asosiy"->Icons.Default.Home;"Qidiruv"->Icons.Default.Search;"Saqlangan"->Icons.Default.Bookmark;"Obuna"->Icons.Default.Star;else->Icons.Default.Person}
        NavigationBarItem(selected=tab==item,onClick={tab=item;query=""},icon={Icon(icon,contentDescription=item)},label={Text(item,fontSize=10.sp)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Gold,selectedTextColor=Gold,indicatorColor=Color(0xFF39301F),unselectedIconColor=Muted,unselectedTextColor=Muted))
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
 if(items.isEmpty()) Box(Modifier.fillMaxWidth().padding(40.dp),contentAlignment=Alignment.Center){Text("Hozircha kino topilmadi",color=Muted)}
 LazyColumn(contentPadding=PaddingValues(start=16.dp,end=16.dp,bottom=12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
  items(items) { film ->
   Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().clickable{onOpen(film)}) {
    Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
     Box(Modifier.size(76.dp,96.dp).background(Color(0xFF293447),RoundedCornerShape(10.dp)),contentAlignment=Alignment.Center) { Icon(Icons.Default.Movie,null,tint=Gold,modifier=Modifier.size(34.dp)) }
     Column(Modifier.weight(1f).padding(start=13.dp)) {
      Text(film.title,color=Color.White,fontWeight=FontWeight.Bold,fontSize=16.sp)
      Text(film.year.toString()+"  •  "+film.genre,color=Muted,fontSize=11.sp,modifier=Modifier.padding(top=5.dp))
      Text("★ "+film.rating,color=Gold,fontWeight=FontWeight.Bold,fontSize=13.sp,modifier=Modifier.padding(top=8.dp))
      Text("Batafsil ko‘rish  ›",color=Gold,fontSize=12.sp,modifier=Modifier.padding(top=7.dp))
     }
     IconButton(onClick={onSave(film)}) { Icon(if(saved.contains(film.id.toString())) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,null,tint=Gold) }
    }
   }
  }
 }
}

@Composable
private fun FilmDetail(film:Film,isSaved:Boolean,onSave:(Film)->Unit,onPlay:(Film)->Unit,onBack:()->Unit) {
 Column(Modifier.fillMaxSize().padding(20.dp)) {
  Text("‹  ORQAGA",color=Gold,fontWeight=FontWeight.Bold,modifier=Modifier.clickable{onBack()}.padding(vertical=8.dp))
  Box(Modifier.fillMaxWidth().height(190.dp).background(Color(0xFF202C3D),RoundedCornerShape(20.dp)),contentAlignment=Alignment.Center) { Icon(Icons.Default.Movie,null,tint=Gold,modifier=Modifier.size(64.dp)) }
  Text(film.title,color=Color.White,fontSize=27.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=18.dp))
  Text(film.year.toString()+"  •  "+film.genre+"  •  ★ "+film.rating,color=Gold,fontSize=13.sp,modifier=Modifier.padding(top=8.dp))
  Text(film.summary,color=Color(0xFFD0D6E0),fontSize=15.sp,lineHeight=23.sp,modifier=Modifier.padding(top=18.dp))
  Button(onClick={onPlay(film)},Modifier.fillMaxWidth().padding(top=24.dp),colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Color.Black)) { Icon(Icons.Default.PlayArrow,null); Text("  TOMOSHA QILISH",fontWeight=FontWeight.Bold) }
  OutlinedButton(onClick={onSave(film)},Modifier.fillMaxWidth().padding(top=8.dp)) { Icon(if(isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,null); Text(if(isSaved)"  Saqlanganlardan olish" else "  Saqlanganlarga qo‘shish") }
  Text("Eslatma: video namuna manbasidan ochiladi. O‘z kino fayllaringizni qo‘shish uchun media manbasi kerak bo‘ladi.",color=Muted,fontSize=11.sp,modifier=Modifier.padding(top=18.dp))
 }
}

@Composable
private fun ProfilePage() {
 Column(Modifier.fillMaxSize().padding(22.dp)) {
  Text("PROFIL",color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp)
  Text("AlfaPlay foydalanuvchisi",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=10.dp))
  Text("Mahalliy profil rejimi",color=Muted,modifier=Modifier.padding(top=6.dp))
  listOf("Ilova versiyasi     1.0.0","Saqlangan kinolar     Qurilmada saqlanadi","Hisob turi     Mehmon").forEach { Text(it,color=Color.White,modifier=Modifier.fillMaxWidth().padding(top=18.dp).background(Panel,RoundedCornerShape(12.dp)).padding(16.dp)) }
  Text("Serverga ulangan hisob hozircha sozlanmagan.",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=18.dp))
 }
}

@Composable
private fun PlansPage() {
 Column(Modifier.fillMaxSize().padding(20.dp)) {
  Text("ALFAPLAY VIP",color=Gold,fontSize=13.sp,fontWeight=FontWeight.Bold)
  Text("Premium imkoniyatlar",color=Color.White,fontSize=25.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp,bottom=16.dp))
  listOf("ALFA START" to "29 000 so‘m / oy","ALFA PRO 4K" to "79 000 so‘m / 3 oy","VIP FAMILY" to "199 000 so‘m / yil").forEach { plan ->
   Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().padding(bottom=12.dp)) {
    Column(Modifier.padding(18.dp)) {
     Text(plan.first,color=Gold,fontWeight=FontWeight.Bold)
     Text(plan.second,color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp))
     Text("Reklamasiz tomosha • Yuqori sifat • Maxsus imkoniyatlar",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=8.dp))
     Text("To‘lov hozircha ilovaga ulanmagan",color=Muted,fontSize=11.sp,modifier=Modifier.padding(top=10.dp))
    }
   }
  }
 }
}
