require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "RealTimeTracker"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  RealTimeTracker
                   DESC
  s.homepage     = "https://github.com/github_account/RealTimeTracker"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.authors      = { "Your Name" => "pablo.segura@aol.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/github_account/RealTimeTracker.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.pod_target_xcconfig = { 'SWIFT_VERSION' => '5.0' }
  s.swift_version = "5.0"
  s.requires_arc = true

  s.dependency "React"
  # ...
  # s.dependency "..."
end

