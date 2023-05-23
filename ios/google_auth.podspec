#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint google_auth.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'google_auth'
  s.version          = '0.0.1'
  s.summary          = 'Google Authentication.'
  s.description      = <<-DESC
Flutter plugin to authenticate with Google.
                       DESC
  s.homepage         = 'http://transang.me'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Tran Sang' => 'me@transa.ng' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.dependency 'GoogleSignIn', '~> 7.0'
  s.platform = :ios, '12.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
